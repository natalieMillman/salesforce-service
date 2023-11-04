package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import com.salesforce.eventbus.protobuf.ReplayPreset;

import java.nio.ByteBuffer;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class PubSubConfiguration {
    @JsonProperty("host")
    private final String host;
    @JsonProperty("port")
    private final Integer port;
    @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest")
    private final Integer numberOfEventsToSubscribeInEachFetchRequest;
    @JsonProperty("plaintextChannel")
    private final Boolean plaintextChannel;
    @JsonProperty("replayPreset")
    private final ReplayPreset replayPreset;
    @JsonProperty("interval")
    private final Integer interval;
    @JsonProperty("replayId")
    private final long replayId;

    public PubSubConfiguration(@JsonProperty("host") String host,
                               @JsonProperty("port") Integer port,
                               @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest") Integer numberOfEventsToSubscribeInEachFetchRequest,
                               @JsonProperty("plaintextChannel") Boolean plaintextChannel,
                               @JsonProperty("replayPreset") ReplayPreset replayPreset,
                               @JsonProperty("interval") Integer interval,
                               @JsonProperty("replayId") long replayId) {
        this.host = host;
        this.port = port;
        this.numberOfEventsToSubscribeInEachFetchRequest = numberOfEventsToSubscribeInEachFetchRequest;
        this.plaintextChannel = plaintextChannel;
        this.replayPreset = replayPreset;
        this.interval = interval;
        this.replayId = replayId;
    }

    @JsonProperty("host")
    public String host() {
        return host;
    }

    @JsonProperty("port")
    public Integer port() {
        return port;
    }

    @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest")
    public Integer numberOfEventsToSubscribeInEachFetchRequest() {
        return numberOfEventsToSubscribeInEachFetchRequest;
    }

    @JsonProperty("plaintextChannel")
    public Boolean plaintextChannel() {
        return plaintextChannel;
    }

    @JsonProperty("replayPreset")
    public ReplayPreset replayPreset() {
        return replayPreset;
    }

    @JsonProperty("interval")
    public Integer interval() {
        return interval;
    }

    @JsonProperty("replayId")
    public long replayId() {
        return replayId;
    }

    public ByteString getReplayId() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(replayId);
        buffer.flip();
        return ByteString.copyFrom(buffer);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PubSubConfiguration) obj;
        return Objects.equals(this.host, that.host) &&
                Objects.equals(this.port, that.port) &&
                Objects.equals(this.numberOfEventsToSubscribeInEachFetchRequest, that.numberOfEventsToSubscribeInEachFetchRequest) &&
                Objects.equals(this.plaintextChannel, that.plaintextChannel) &&
                Objects.equals(this.replayPreset, that.replayPreset) &&
                Objects.equals(this.interval, that.interval) &&
                Objects.equals(this.replayId, that.replayId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, numberOfEventsToSubscribeInEachFetchRequest, plaintextChannel, replayPreset, interval, replayId);
    }

    @Override
    public String toString() {
        return "PubSubConfiguration[" +
                "host=" + host + ", " +
                "port=" + port + ", " +
                "numberOfEventsToSubscribeInEachFetchRequest=" + numberOfEventsToSubscribeInEachFetchRequest + ", " +
                "plaintextChannel=" + plaintextChannel + ", " +
                "replayPreset=" + replayPreset + ", " +
                "interval=" + interval + ", " +
                "replayId=" + replayId + ']';
    }

}
