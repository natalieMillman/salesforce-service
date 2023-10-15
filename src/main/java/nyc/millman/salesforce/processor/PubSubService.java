package nyc.millman.salesforce.processor;

import java.io.*;
import java.nio.ByteBuffer;

import nyc.millman.salesforce.api.PubSubMode;
import nyc.millman.salesforce.api.SalesforceCredentials;
import nyc.millman.salesforce.api.configuration.SubscriberConfiguration;
import nyc.millman.salesforce.external.client.SalesforceClient;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.ByteString;
import com.salesforce.eventbus.protobuf.*;
import io.grpc.*;

public class PubSubService implements AutoCloseable {

    protected static final Logger logger = LoggerFactory.getLogger(PubSubService.class.getClass());

    protected final String grpcHost;
    protected final int grpcPort;
    protected final ManagedChannel channel;
    protected final PubSubGrpc.PubSubStub asyncStub;
    protected final PubSubGrpc.PubSubBlockingStub blockingStub;
    protected final SalesforceClient client;
    protected final SubscriberConfiguration configuration;
    protected String tenantGuid;
    protected String busTopicName;
    protected TopicInfo topicInfo;
    protected SchemaInfo schemaInfo;
    protected String sessionToken;

    public PubSubService(SubscriberConfiguration configuration, SalesforceClient client) {
        this.configuration = configuration;
        this.client = client;
        this.grpcHost = configuration.pubsubHost();
        this.grpcPort = configuration.pubsubPort();
        this.channel = buildChannel(grpcHost, grpcPort);
        logger.info("Using grpcHost {} and grpcPort {}", grpcHost, grpcPort);

        SalesforceCredentials credentials = new SalesforceCredentials(client.getToken());

        asyncStub = PubSubGrpc.newStub(channel).withCallCredentials(credentials);
        blockingStub = PubSubGrpc.newBlockingStub(channel).withCallCredentials(credentials);
    }

    protected void setupTopicDetails(final String topicName, final PubSubMode pubSubMode, final boolean fetchSchema) {
        if (topicName != null && !topicName.isEmpty()) {
            try {
                topicInfo = blockingStub.getTopic(TopicRequest.newBuilder().setTopicName(topicName).build());
                tenantGuid = topicInfo.getTenantGuid();
                busTopicName = topicInfo.getTopicName();
                if (fetchSchema) {
                    SchemaRequest schemaRequest = SchemaRequest.newBuilder().setSchemaId(topicInfo.getSchemaId())
                            .build();
                    schemaInfo = blockingStub.getSchema(schemaRequest);
                }
            } catch (final Exception ex) {
                logger.error("Error during fetching topic", ex);
                close();
                throw ex;
            }
        }
    }

    public static ByteString getReplayIdFromLong(long replayValue) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(replayValue);
        buffer.flip();

        return ByteString.copyFrom(buffer);
    }

    public GenericRecord createCarMaintenanceRecord(Schema schema) {
        // Please remember to use the appropriate orgId for the CreatedById field.
        return new GenericRecordBuilder(schema).set("CreatedDate", System.currentTimeMillis() / 1000)
                .set("CreatedById", "005xx000001Svwo").set("Mileage__c", 95443.0).set("Cost__c", 99.40)
                .set("WorkDescription__c", "Replaced front brakes").build();
    }

    public GenericRecord createCarMaintenanceRecord(Schema schema, final int counter) {
        // Please remember to use the appropriate orgId for the CreatedById field.
        return new GenericRecordBuilder(schema).set("CreatedDate", System.currentTimeMillis() / 1000)
                .set("CreatedById", "005xx000001Svwo").set("Mileage__c", 95443.0).set("Cost__c", 99.40)
                .set("WorkDescription__c", "Replaced front brakes; event: " + counter).build();
    }

    public static void printStatusRuntimeException(final String context, final Exception e) {
        logger.error(context);

        if (e instanceof StatusRuntimeException) {
            final StatusRuntimeException expected = (StatusRuntimeException)e;
            logger.error(" === GRPC Exception ===", e);
            Metadata trailers = ((StatusRuntimeException)e).getTrailers();
            logger.error(" === Trailers ===");
            trailers.keys().stream().forEach(t -> {
                logger.error("[Trailer] = " + t + " [Value] = "
                        + trailers.get(Metadata.Key.of(t, Metadata.ASCII_STRING_MARSHALLER)));
            });
        } else {
            logger.error(" === Exception ===", e);
        }
    }

    public static GenericRecord deserialize(Schema schema, ByteString payload) throws IOException {
        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
        ByteArrayInputStream in = new ByteArrayInputStream(payload.toByteArray());
        BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(in, null);
        return reader.read(null, decoder);
    }

    private ManagedChannel buildChannel(String grpcHost, int grpcPort){
        if (configuration.plaintextChannel()) {
            return ManagedChannelBuilder.forAddress(grpcHost, grpcPort).usePlaintext().build();
        } else {
            return ManagedChannelBuilder.forAddress(grpcHost, grpcPort).build();
        }
    }

    public String getSessionToken() {

        return sessionToken;
    }

    @Override
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (Throwable t) {
                logger.warn("Cannot stop session HTTP client", t);
            }
        }
        try {
            channel.shutdown();
        } catch (Throwable t) {
            logger.warn("Cannot shutdown GRPC channel", t);
        }
    }
}