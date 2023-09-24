package nyc.millman.salesforce.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.core.Configuration;
import nyc.millman.salesforce.api.SalesforceAuthenticationConfiguration;
import nyc.millman.salesforce.api.SalesforceClientConfiguration;

public class SalesforceServiceConfiguration extends Configuration {

    @JsonProperty("salesforceClientConfiguration")
    private final SalesforceClientConfiguration salesforceClientConfiguration;

    @JsonProperty("salesforceAuthenticationConfiguration")
    private final SalesforceAuthenticationConfiguration salesforceAuthenticationConfiguration;

    public SalesforceServiceConfiguration(
            @JsonProperty("salesforceClientConfiguration") SalesforceClientConfiguration salesforceClientConfiguration,
            @JsonProperty("salesforceAuthenticationConfiguration") SalesforceAuthenticationConfiguration salesforceAuthenticationConfiguration){
        this.salesforceClientConfiguration = salesforceClientConfiguration;
        this.salesforceAuthenticationConfiguration = salesforceAuthenticationConfiguration;
    }

    @JsonProperty("salesforceClientConfiguration")
    public SalesforceClientConfiguration getSalesforceClientConfiguration() {
        return salesforceClientConfiguration;
    }

    @JsonProperty("salesforceAuthenticationConfiguration")
    public SalesforceAuthenticationConfiguration getSalesforceAuthenticationConfiguration(){
        return salesforceAuthenticationConfiguration;
    }

}
