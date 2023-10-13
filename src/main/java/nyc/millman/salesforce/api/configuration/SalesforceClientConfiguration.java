package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SalesforceClientConfiguration {

    @JsonProperty("baseUrl")
    private final String baseUrl;

    @JsonProperty("timeout")
    private final long timeout;

    public SalesforceClientConfiguration(
            @JsonProperty("baseUrl") String baseUrl,
            @JsonProperty("timeout") long timeout
    ){
        this.baseUrl = baseUrl;
        this.timeout = timeout;
    }

    @JsonProperty("baseUrl")
    public String getBaseUrl() {
        return baseUrl;
    }

    @JsonProperty("timeout")
    public long getTimeout() {
        return timeout;
    }

    @Override
    public String toString(){
        return "{" +
                "baseUrl:\"" + baseUrl + "\"," +
                "timeout:\"" + timeout + "\"" +
                "}";
    }

    @Override
    public int hashCode(){
        return Objects.hash(baseUrl, timeout);
    }
}
