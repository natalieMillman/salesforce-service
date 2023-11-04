package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.protobuf.ByteString;
import com.salesforce.eventbus.protobuf.ReplayPreset;

import java.nio.ByteBuffer;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SubscriberConfiguration {
    @JsonProperty
    private final String host;
    @JsonProperty
    private final Integer port;
    @JsonProperty
    private final String topic;
    @JsonProperty
    private final Integer numberOfEventsToSubscribeInEachFetchRequest;
    @JsonProperty
    private final Boolean plaintextChannel;
    @JsonProperty
    private final ReplayPreset replayPreset;
    @JsonProperty
    private final long replayId;

    public SubscriberConfiguration(
            @JsonProperty("host")  String host,
            @JsonProperty("port")  Integer port,
            @JsonProperty("topic")  String topic,
            @JsonProperty("numberOfEventsToSubscribeInEachFetchRequest")  Integer numberOfEventsToSubscribeInEachFetchRequest,
            @JsonProperty("plaintextChannel")  Boolean plaintextChannel,
            @JsonProperty("replayPreset")  ReplayPreset replayPreset,
            @JsonProperty("replayId") long replayId
    ) {
        this.host = host;
        this.port = port;
        this.topic = topic;
        this.numberOfEventsToSubscribeInEachFetchRequest = numberOfEventsToSubscribeInEachFetchRequest;
        this.plaintextChannel = plaintextChannel;
        this.replayPreset = replayPreset;
        this.replayId = replayId;
    }

    @JsonProperty
    public String getHost() {
        return host;
    }

    @JsonProperty
    public Integer getPort() {
        return port;
    }

    @JsonProperty
    public String getTopic() {
        return topic;
    }

    @JsonProperty
    public Integer getNumberOfEventsToSubscribeInEachFetchRequest() {
        return numberOfEventsToSubscribeInEachFetchRequest;
    }

    @JsonProperty
    public Boolean getPlaintextChannel() {
        return plaintextChannel;
    }

    @JsonProperty
    public ReplayPreset getReplayPreset() {
        return replayPreset;
    }

    @JsonProperty
    public ByteString getReplayId() {
        return getReplayIdFromLong(replayId);
    }

    private static ByteString getReplayIdFromLong(long replayValue) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(replayValue);
        buffer.flip();

        return ByteString.copyFrom(buffer);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SubscriberConfiguration) obj;
        return Objects.equals(this.host, that.host) &&
                Objects.equals(this.port, that.port) &&
                Objects.equals(this.topic, that.topic) &&
                Objects.equals(this.numberOfEventsToSubscribeInEachFetchRequest, that.numberOfEventsToSubscribeInEachFetchRequest) &&
                Objects.equals(this.plaintextChannel, that.plaintextChannel) &&
                Objects.equals(this.replayPreset, that.replayPreset) &&
                Objects.equals(this.replayId, that.replayId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port, topic, numberOfEventsToSubscribeInEachFetchRequest, plaintextChannel, replayPreset, replayId);
    }

    @Override
    public String toString() {
        return "\"SubscriberConfiguration\":{" +
                "\"host\":\"" + host + ":\", " +
                "\"port\":\"" + port + ":\", " +
                "\"topic\":\"" + topic + ":\", " +
                "\"numberOfEventsToSubscribeInEachFetchRequest\":\"" + numberOfEventsToSubscribeInEachFetchRequest + ":\", " +
                "\"plaintextChannel\":\"" + plaintextChannel + ":\", " +
                "\"replayPreset\":\"" + replayPreset + ":\", " +
                "\"replayId\":\"" + replayId + ":\"}";
    }

}
