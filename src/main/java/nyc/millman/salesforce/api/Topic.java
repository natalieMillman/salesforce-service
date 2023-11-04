package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Topic {

    @JsonProperty("name")
    private final String name;
    @JsonProperty("path")
    private final String path;
    @JsonProperty("replayId")
    private final long replayId;
    @JsonProperty("interval")
    private final long interval;

    public Topic(
            @JsonProperty("name") String name,
            @JsonProperty("path") String path,
            @JsonProperty("replayId") long replayId,
            @JsonProperty("interval") long interval) {
        this.name = name;
        this.path = path;
        this.replayId = replayId;
        this.interval = interval;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @JsonProperty("replayId")
    public long getReplayId() {
        return replayId;
    }

    @JsonProperty("interval")
    public long getInterval() {
        return interval;
    }

    @Override
    public String toString() {
        return "\"Topic\":{" +
                "\"name\":\"" + name + "\"" +
                ", \"path\":\"" + path + "\"" +
                ", \"replayId\":" + replayId +
                ", \"interval\":" + interval +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Topic)) return false;
        var that = (Topic) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.path, that.path) &&
                this.replayId == that.replayId &&
                this.interval == that.interval;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path, replayId, interval);
    }

}
