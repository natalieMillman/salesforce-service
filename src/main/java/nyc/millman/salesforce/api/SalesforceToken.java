package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SalesforceToken(
        @JsonProperty("id") String id,
        @JsonProperty("issued_at") Instant issuedAt,
        @JsonProperty("instance_url") String instanceUrl,
        @JsonProperty("signature") String signature,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType) {
}
