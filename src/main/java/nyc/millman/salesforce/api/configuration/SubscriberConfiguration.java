package nyc.millman.salesforce.api.configuration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import nyc.millman.salesforce.api.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class SubscriberConfiguration {

    private final Logger logger = LoggerFactory.getLogger(SubscriberConfiguration.class);

    @JsonProperty("topics")
    private final Set<Topic> topics;

    public SubscriberConfiguration(
            @JsonProperty("topics") Set<Topic> topics) {
        this.topics = topics;
    }

    @JsonProperty("topics")
    public Set<Topic> getTopics() {
        logger.info("SubscriberConfiguration.getTopics() returning: " + topics);
        return topics;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SubscriberConfiguration) obj;
        return Objects.equals(this.topics, that.topics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(topics);
    }

    @Override
    public String toString() {
        return "\"SubscriberConfiguration\":{\"topics\":[" + topics + "]";
    }

}
