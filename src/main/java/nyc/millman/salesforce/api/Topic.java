package nyc.millman.salesforce.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class Topic {

    @JsonProperty("name")
    private final String name;
    @JsonProperty("path")
    private final String path;

    public Topic(
            @JsonProperty("name") String name,
            @JsonProperty("path") String path) {
        this.name = name;
        this.path = path;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("path")
    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "\"Topic\":{" +
                "\"name\":\"" + name + "\"" +
                ", \"path\":\"" + path + "\"" +
                "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Topic)) return false;
        var that = (Topic) obj;
        return Objects.equals(this.name, that.name) &&
                Objects.equals(this.path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, path);
    }

}
