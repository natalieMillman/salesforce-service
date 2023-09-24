package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SalesforceToken(
        @JsonProperty String accessToken,
        @JsonProperty("issued_at") Instant issuedAt,
        @JsonProperty String orgId,
        @JsonProperty String instanceUrl) {
}
