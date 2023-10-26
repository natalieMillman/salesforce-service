package nyc.millman.salesforce.processor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.grpc.Metadata;
import io.grpc.StatusRuntimeException;

import nyc.millman.salesforce.api.configuration.SubscriberConfiguration;
import nyc.millman.salesforce.external.client.SalesforceClient;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;

import com.google.protobuf.ByteString;
import com.salesforce.eventbus.protobuf.*;

import io.grpc.stub.StreamObserver;

import static nyc.millman.salesforce.api.PubSubMode.SUBSCRIBE;

public class Subscriber extends PubSubService {

    private static int batchSize;
    private static final int MAX_RETRIES = 3;
    private static final String ERROR_REPLAY_ID_VALIDATION_FAILED = "fetch.replayid.validation.failed";
    private static final String ERROR_REPLAY_ID_INVALID = "fetch.replayid.corrupted";
    private static final String ERROR_SERVICE_UNAVAILABLE = "service.unavailable";
    public static int SERVICE_UNAVAILABLE_WAIT_BEFORE_RETRY_SECONDS = 5;
    private final SubscriberConfiguration subscriberConfiguration;

    private final SalesforceClient salesforceClient;
    public static AtomicBoolean isActive = new AtomicBoolean(false);
    public static AtomicInteger retriesLeft = new AtomicInteger(MAX_RETRIES);
    private StreamObserver<FetchRequest> serverStream;
    private Map<String, Schema> schemaCache = new ConcurrentHashMap<>();
    private AtomicInteger receivedEvents = new AtomicInteger(0);
    private final StreamObserver<FetchResponse> responseStreamObserver;
    private final ReplayPreset replayPreset;
    private final ByteString customReplayId;
    private final ExecutorService eventProcessingExecutors;
    private final ScheduledExecutorService retryScheduler;
    private volatile ByteString storedReplay;

    public Subscriber(SubscriberConfiguration subscriberConfiguration, SalesforceClient salesforceClient) {
        super(subscriberConfiguration, salesforceClient);
        isActive.set(true);
        this.subscriberConfiguration = subscriberConfiguration;
        this.salesforceClient = salesforceClient;
        this.batchSize = Math.min(5, subscriberConfiguration.getNumberOfEventsToSubscribeInEachFetchRequest());
        this.responseStreamObserver = getDefaultResponseStreamObserver();
        this.replayPreset = subscriberConfiguration.getReplayPreset();
        this.customReplayId = subscriberConfiguration.getReplayId();
        this.retryScheduler = Executors.newScheduledThreadPool(1);
        this.eventProcessingExecutors = Executors.newFixedThreadPool(3);
        this.setupTopicDetails(subscriberConfiguration.getTopic(), SUBSCRIBE, false);
    }

    public void startSubscription() {
        logger.info("Subscription started for topic: " + busTopicName + ".");
        fetch(batchSize, busTopicName, replayPreset, customReplayId);
        // Thread being blocked here for demonstration of this specific example. Blocking the thread in production is not recommended.
        while(isActive.get()) {
            waitInMillis(5_000);
            logger.info("Subscription Active. Received " + receivedEvents.get() + " events.");
        }
    }

    public void fetch(int providedBatchSize, String providedTopicName, ReplayPreset providedReplayPreset, ByteString providedReplayId) {
        serverStream = asyncStub.subscribe(this.responseStreamObserver);
        FetchRequest.Builder fetchRequestBuilder = FetchRequest.newBuilder()
                .setNumRequested(providedBatchSize)
                .setTopicName(providedTopicName)
                .setReplayPreset(providedReplayPreset);
        if (providedReplayPreset == ReplayPreset.CUSTOM) {
            logger.info("Subscription has Replay Preset set to CUSTOM. In this case, the events will be delivered from provided ReplayId.");
            fetchRequestBuilder.setReplayId(providedReplayId);
        }
        serverStream.onNext(fetchRequestBuilder.build());
    }

    public long getBackoffWaitTime() {
        long waitTime = (long) (Math.pow(2, MAX_RETRIES - retriesLeft.get()) * 1000);
        return waitTime;
    }

    public void waitInMillis(long duration) {
        synchronized (this) {
            try {
                this.wait(duration);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private StreamObserver<FetchResponse> getDefaultResponseStreamObserver() {
        return new StreamObserver<FetchResponse>() {
            @Override
            public void onNext(FetchResponse fetchResponse) {
                logger.info("Received batch of " + fetchResponse.getEventsList().size() + " events");
                logger.info("RPC ID: " + fetchResponse.getRpcId());
                for(ConsumerEvent ce : fetchResponse.getEventsList()) {
                    try {
                        // Unless the schema of the event is available in the local schema cache, there is a blocking
                        // GetSchema call being made to obtain the schema which may block the thread. Therefore, the
                        // processing of events is done asynchronously.
                        CompletableFuture.runAsync(new EventProcessor(ce), eventProcessingExecutors);
                    } catch (Exception e) {
                        logger.info(e.toString());
                    }
                    receivedEvents.addAndGet(1);
                }
                // Latest replayId stored for any future FetchRequests with CUSTOM ReplayPreset.
                // NOTE: Replay IDs are opaque in nature and should be stored and used as bytes without any conversion.
                storedReplay = fetchResponse.getLatestReplayId();

                // Reset retry count
                if (retriesLeft.get() != MAX_RETRIES) {
                    retriesLeft.set(MAX_RETRIES);
                }

                // Implementing a basic flow control strategy where the next fetchRequest is sent only after the
                // requested number of events in the previous fetchRequest(s) are received.
                // NOTE: This block may need to be implemented before the processing of events if event processing takes
                // a long time. There is a 70s timeout period during which, if pendingNumRequested is 0 and no events are
                // further requested then the stream will be closed.
                if (fetchResponse.getPendingNumRequested() == 0) {
                    fetchMore(batchSize);
                }
            }

            @Override
            public void onError(Throwable t) {
                printStatusRuntimeException("Error during Subscribe", (Exception) t);
                logger.info("Retries remaining: " + retriesLeft.get());
                if (retriesLeft.get() == 0) {
                    logger.info("Exhausted all retries. Closing Subscription.");
                    isActive.set(false);
                } else {
                    retriesLeft.decrementAndGet();
                    Metadata trailers = ((StatusRuntimeException)t).getTrailers() != null ? ((StatusRuntimeException)t).getTrailers() : null;
                    String errorCode = (trailers != null && trailers.get(Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER)) != null) ?
                            trailers.get(Metadata.Key.of("error-code", Metadata.ASCII_STRING_MARSHALLER)) : null;

                    // Closing the old stream for sanity
                    serverStream.onCompleted();

                    ReplayPreset retryReplayPreset = ReplayPreset.LATEST;
                    ByteString retryReplayId = null;
                    long retryDelay = 0;

                    // Retry strategies that can be implemented based on the error type.
                    if (errorCode.contains(ERROR_REPLAY_ID_VALIDATION_FAILED) || errorCode.contains(ERROR_REPLAY_ID_INVALID)) {
                        logger.info("Invalid or no replayId provided in FetchRequest for CUSTOM Replay. Trying again with EARLIEST Replay.");
                        retryDelay = getBackoffWaitTime();
                        retryReplayPreset = ReplayPreset.EARLIEST;
                    } else if (errorCode.contains(ERROR_SERVICE_UNAVAILABLE)) {
                        logger.info("Service currently unavailable. Trying again with LATEST Replay.");
                        retryDelay = SERVICE_UNAVAILABLE_WAIT_BEFORE_RETRY_SECONDS * 1000;
                    } else {
                        retryDelay = getBackoffWaitTime();
                        if (storedReplay != null) {
                            logger.info("Retrying with Stored Replay.");
                            retryReplayPreset = ReplayPreset.CUSTOM;
                            retryReplayId = getStoredReplay();
                        } else {
                            logger.info("Retrying with LATEST Replay.");;
                        }

                    }
                    logger.info("Retrying in " + retryDelay + "ms.");
                    retryScheduler.schedule(new RetryRequestSender(retryReplayPreset, retryReplayId), retryDelay, TimeUnit.MILLISECONDS);
                }
            }

            @Override
            public void onCompleted() {
                logger.info("Call completed by server. Closing Subscription.");
                isActive.set(false);
            }
        };
    }

    private class RetryRequestSender implements Runnable {
        private ReplayPreset retryReplayPreset;
        private ByteString retryReplayId;
        public RetryRequestSender(ReplayPreset replayPreset, ByteString replayId) {
            this.retryReplayPreset = replayPreset;
            this.retryReplayId = replayId;
        }

        @Override
        public void run() {
            fetch(batchSize, busTopicName, retryReplayPreset, retryReplayId);
            logger.info("Retry FetchRequest Sent.");
        }
    }

    private class EventProcessor implements Runnable {
        private ConsumerEvent ce;
        public EventProcessor(ConsumerEvent consumerEvent) {
            this.ce = consumerEvent;
        }

        @Override
        public void run() {
            try {
                processEvent(ce);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processEvent(ConsumerEvent ce) throws IOException {
        Schema writerSchema = getSchema(ce.getEvent().getSchemaId());
        this.storedReplay = ce.getReplayId();
        GenericRecord record = deserialize(writerSchema, ce.getEvent().getPayload());
        logger.info("Received event with payload: " + record.toString());
    }

    public Schema getSchema(String schemaId) {
        return schemaCache.computeIfAbsent(schemaId, id -> {
            SchemaRequest request = SchemaRequest.newBuilder().setSchemaId(id).build();
            String schemaJson = blockingStub.getSchema(request).getSchemaJson();
            return (new Schema.Parser()).parse(schemaJson);
        });
    }

    public void fetchMore(int numEvents) {
        FetchRequest fetchRequest = FetchRequest.newBuilder().setTopicName(this.busTopicName)
                .setNumRequested(numEvents).build();
        serverStream.onNext(fetchRequest);
    }

    public AtomicInteger getReceivedEvents() {
        return receivedEvents;
    }

    public void updateReceivedEvents(int delta) {
        receivedEvents.addAndGet(delta);
    }

    public int getBatchSize() {
        return batchSize;
    }
    public ByteString getStoredReplay() {
        return storedReplay;
    }

    public void setStoredReplay(ByteString storedReplay) {
        this.storedReplay = storedReplay;
    }

    @Override
    public synchronized void close() {
        try {
            if (serverStream != null) {
                serverStream.onCompleted();
            }
            if (retryScheduler != null) {
                retryScheduler.shutdown();
            }
            if (eventProcessingExecutors != null) {
                eventProcessingExecutors.shutdown();
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
        super.close();
    }



}
