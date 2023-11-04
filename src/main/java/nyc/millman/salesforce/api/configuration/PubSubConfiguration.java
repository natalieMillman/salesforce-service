package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.salesforce.eventbus.protobuf.ReplayPreset;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PubSubConfiguration(@JsonProperty("host") String host,
                                  @JsonProperty("port") Integer port,
                                  @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest") Integer numberOfEventsToSubscribeInEachFetchRequest,
                                  @JsonProperty("plaintextChannel") Boolean plaintextChannel,
                                  @JsonProperty("replayPreset") ReplayPreset replayPreset,
                                  @JsonProperty("replayId") long replayId) {
}
