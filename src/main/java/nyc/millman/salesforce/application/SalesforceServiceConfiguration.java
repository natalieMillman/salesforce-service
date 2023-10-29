package nyc.millman.salesforce.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import nyc.millman.salesforce.api.configuration.GRPCHostConfiguration;
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

    @JsonProperty
    public final GRPCHostConfiguration grpcConfiguration;

    public SalesforceServiceConfiguration(
            @JsonProperty("salesforceClientConfiguration") SalesforceClientConfiguration salesforceClientConfiguration,
            @JsonProperty("salesforceAuthenticationConfiguration") SalesforceAuthenticationConfiguration salesforceAuthenticationConfiguration,
            @JsonProperty("subscriberConfiguration") SubscriberConfiguration subscriberConfiguration,
            @JsonProperty("grpcConfiguration") GRPCHostConfiguration grpcConfiguration){
        this.salesforceClientConfiguration = salesforceClientConfiguration;
        this.salesforceAuthenticationConfiguration = salesforceAuthenticationConfiguration;
        this.subscriberConfiguration = subscriberConfiguration;
        this.grpcConfiguration = grpcConfiguration;
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

    @JsonProperty("grpcConfiguration")
    public GRPCHostConfiguration getGrpcConfiguration() {
        return grpcConfiguration;
    }

}
