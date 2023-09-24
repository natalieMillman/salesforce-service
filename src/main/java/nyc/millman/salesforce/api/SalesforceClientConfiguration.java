package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class SalesforceClientConfiguration {

    @JsonProperty("baseUrl")
    private final String baseUrl;

    public SalesforceClientConfiguration(
            @JsonProperty("baseUrl") String baseUrl
    ){
        this.baseUrl = baseUrl;
    }

    @JsonProperty("baseUrl")
    public String getBaseUrl() {
        return baseUrl;
    }


    @Override
    public String toString(){
        return "{" +
                "baseUrl:\"" + baseUrl + "\"" +
                "}";
    }

    @Override
    public int hashCode(){
        return Objects.hash(baseUrl);
    }
}
