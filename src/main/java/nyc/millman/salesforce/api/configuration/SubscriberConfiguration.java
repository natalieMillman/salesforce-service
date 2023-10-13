package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import com.salesforce.eventbus.protobuf.ReplayPreset;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubscriberConfiguration(
        @JsonProperty String username,
        @JsonProperty String password,
        @JsonProperty String loginUrl,
        @JsonProperty String tenantId,
        @JsonProperty String accessToken,
        @JsonProperty String pubsubHost,
        @JsonProperty Integer pubsubPort,
        @JsonProperty String topic,
        @JsonProperty Integer numberOfEventsToPublish,
        @JsonProperty Integer numberOfEventsToSubscribeInEachFetchRequest,
        @JsonProperty Boolean plaintextChannel,
        @JsonProperty Boolean providedLoginUrl,
        @JsonProperty ReplayPreset replayPreset,
        @JsonProperty ByteString replayId
) {
}
