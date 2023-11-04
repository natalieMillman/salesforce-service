package nyc.millman.salesforce.processor;

import nyc.millman.salesforce.api.Topic;
import nyc.millman.salesforce.api.configuration.PubSubConfiguration;
import nyc.millman.salesforce.api.configuration.SubscriberConfiguration;
import nyc.millman.salesforce.external.client.SalesforceClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static nyc.millman.salesforce.processor.PubSubService.logger;
import static nyc.millman.salesforce.processor.PubSubService.printStatusRuntimeException;

public class SubscriberFactory {

    private final PubSubConfiguration configuration;
    private final SalesforceClient client;
    private final SubscriberConfiguration subscriberConfiguration;
    private final ExecutorService service;

    public SubscriberFactory(PubSubConfiguration configuration, SubscriberConfiguration subscriberConfiguration, SalesforceClient client) {
        this.configuration = configuration;
        this.client = client;
        this.subscriberConfiguration = subscriberConfiguration;
        this.service = Executors.newFixedThreadPool(subscriberConfiguration.getTopics().size());
    }

    public void registerResources() {
        Set<Topic> topics = subscriberConfiguration.getTopics();
        topics.forEach(topic -> {
            logger.info("Registering Subscriber for Topic: " + topic.getPath());
            service.submit(new SubscriberCallable(configuration, client, topic.getPath()));
        });
    }

}
