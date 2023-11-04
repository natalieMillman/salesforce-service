package nyc.millman.salesforce.processor;

import nyc.millman.salesforce.api.Topic;
import nyc.millman.salesforce.api.configuration.PubSubConfiguration;
import nyc.millman.salesforce.api.configuration.SubscriberConfiguration;
import nyc.millman.salesforce.external.client.SalesforceClient;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static nyc.millman.salesforce.processor.PubSubService.logger;
import static nyc.millman.salesforce.processor.PubSubService.printStatusRuntimeException;

public class SubscriberFactory {

    private final PubSubConfiguration configuration;
    private final SalesforceClient client;
    private final SubscriberConfiguration subscriberConfiguration;

    public SubscriberFactory(PubSubConfiguration configuration, SubscriberConfiguration subscriberConfiguration, SalesforceClient client) {
        this.configuration = configuration;
        this.client = client;
        this.subscriberConfiguration = subscriberConfiguration;
    }

    public void registerResources() {
        Set<Topic> topics = subscriberConfiguration.getTopics();
        logger.info("SubscriberFactory.registerResources() topics: " + topics);
        topics.forEach(topic -> {
            try {
                logger.info("Registering Subscriber for Topic: " + topic.getPath());
                var subscriber = new Subscriber(configuration, client, topic.getPath());
                subscriber.startSubscription();
            } catch (Exception e) {
                printStatusRuntimeException("Error during Register Resource", e);
            }
        });
        logger.info("SubscriberFactory.registerResources() completed");
    }

}
