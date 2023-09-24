package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@JsonInclude(NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)

public record Opportunity(
        @JsonProperty("name") String name,
        @JsonProperty("accountId") String accountId,
        @JsonProperty("probability") String probability,
        @JsonProperty("stageName") String stageName
        ) {
}