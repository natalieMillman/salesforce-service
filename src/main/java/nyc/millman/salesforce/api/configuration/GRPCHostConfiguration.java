package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class GRPCHostConfiguration {

    @JsonProperty("host")
    private final String host;
    @JsonProperty("port")
    private final int port;
    @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest")
    private final int numberOfEventsToSubscribeInEachFetchRequest;
    @JsonProperty("replayId")
    private final int replayId;
    @JsonProperty("plaintextChannel")
    private final Boolean plaintextChannel;

    public GRPCHostConfiguration(String host, int port, int numberOfEventsToSubscribeInEachFetchRequest, int replayId, Boolean plaintextChannel) {
        this.host = host;
        this.port = port;
        this.numberOfEventsToSubscribeInEachFetchRequest = numberOfEventsToSubscribeInEachFetchRequest;
        this.replayId = replayId;
        this.plaintextChannel = plaintextChannel;
    }

    @JsonProperty("host")
    public String getHost() {
        return host;
    }

    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest")
    public int getNumberOfEventsToSubscribeInEachFetchRequest() {
        return numberOfEventsToSubscribeInEachFetchRequest;
    }

    @JsonProperty("replayId")
    public int getReplayId() {
        return replayId;
    }

    @JsonProperty("plaintextChannel")
    public Boolean getPlaintextChannel() {
        return plaintextChannel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, numberOfEventsToSubscribeInEachFetchRequest, replayId, plaintextChannel);
    }

}
