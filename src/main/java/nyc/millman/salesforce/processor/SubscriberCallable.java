package nyc.millman.salesforce.processor;

import nyc.millman.salesforce.api.configuration.PubSubConfiguration;
import nyc.millman.salesforce.external.client.SalesforceClient;

import java.util.concurrent.Callable;

import static nyc.millman.salesforce.processor.PubSubService.logger;
import static nyc.millman.salesforce.processor.PubSubService.printStatusRuntimeException;

public class SubscriberCallable implements Callable<Object> {

    private final PubSubConfiguration configuration;
    private final SalesforceClient client;
    private final String topicPath;

    public SubscriberCallable(PubSubConfiguration configuration, SalesforceClient client, String topicPath) {
        this.configuration = configuration;
        this.client = client;
        this.topicPath = topicPath;
    }

    @Override
    public Object call() {
        try {
            logger.info("Registering Subscriber for Topic: " + topicPath);
            var subscriber = new Subscriber(configuration, client, topicPath);
            subscriber.startSubscription();
        } catch (Exception e) {
            printStatusRuntimeException("Error during Register Resource", e);
        }
        return null;
    }
}
