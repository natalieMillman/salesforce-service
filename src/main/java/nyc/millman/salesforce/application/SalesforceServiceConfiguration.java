package nyc.millman.salesforce.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import nyc.millman.salesforce.api.configuration.PubSubConfiguration;
import nyc.millman.salesforce.api.configuration.SalesforceAuthenticationConfiguration;
import nyc.millman.salesforce.api.configuration.SalesforceClientConfiguration;
import nyc.millman.salesforce.api.configuration.SubscriberConfiguration;

public class SalesforceServiceConfiguration extends Configuration {

    @JsonProperty("salesforceClientConfiguration")
    private final SalesforceClientConfiguration salesforceClientConfiguration;

    @JsonProperty("salesforceAuthenticationConfiguration")
    private final SalesforceAuthenticationConfiguration salesforceAuthenticationConfiguration;

    @JsonProperty("subscriberConfiguration")
    private final SubscriberConfiguration subscriberConfiguration;

    @JsonProperty("pubSubConfiguration")
    private final PubSubConfiguration pubSubConfiguration;

    public SalesforceServiceConfiguration(
            @JsonProperty("salesforceClientConfiguration") SalesforceClientConfiguration salesforceClientConfiguration,
            @JsonProperty("salesforceAuthenticationConfiguration") SalesforceAuthenticationConfiguration salesforceAuthenticationConfiguration,
            @JsonProperty("subscriberConfiguration") SubscriberConfiguration subscriberConfiguration,
            @JsonProperty("pubSubConfiguration") PubSubConfiguration pubSubConfiguration){
        this.salesforceClientConfiguration = salesforceClientConfiguration;
        this.salesforceAuthenticationConfiguration = salesforceAuthenticationConfiguration;
        this.subscriberConfiguration = subscriberConfiguration;
        this.pubSubConfiguration = pubSubConfiguration;
    }

    @JsonProperty("salesforceClientConfiguration")
    public SalesforceClientConfiguration getSalesforceClientConfiguration() {
        return salesforceClientConfiguration;
    }

    @JsonProperty("salesforceAuthenticationConfiguration")
    public SalesforceAuthenticationConfiguration getSalesforceAuthenticationConfiguration(){
        return salesforceAuthenticationConfiguration;
    }

    @JsonProperty("subscriberConfiguration")
    public SubscriberConfiguration getSubscriberConfiguration(){
        return subscriberConfiguration;
    }

    @JsonProperty("pubSubConfiguration")
    public PubSubConfiguration getPubSubConfiguration(){
        return pubSubConfiguration;
    }

}
