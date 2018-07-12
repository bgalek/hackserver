package pl.allegro.experiments.chi.chiserver.infrastructure.avro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public class AvroEventDefinition {
    private final String experimentId;
    private final String category;
    private final String action;
    private final String value;
    private final String label;
    private final String boxName;
    private final Instant sentAt;
    private final Instant stateFrom;

    @JsonCreator
    public AvroEventDefinition(
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("category") String category,
            @JsonProperty("action") String action,
            @JsonProperty("value") String value,
            @JsonProperty("label") String label,
            @JsonProperty("boxName") String boxName,
            @JsonProperty("sentAt") Instant sentAt,
            @JsonProperty("__timestamp") Instant stateFrom
            ) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(sentAt);
        Preconditions.checkNotNull(stateFrom);
        Preconditions.checkArgument(experimentId.length() > 0);

        this.experimentId = experimentId;
        this.category = Optional.ofNullable(category).orElse("");
        this.action = Optional.ofNullable(action).orElse("");
        this.value = Optional.ofNullable(value).orElse("");
        this.label = Optional.ofNullable(label).orElse("");
        this.boxName = Optional.ofNullable(boxName).orElse("");
        this.sentAt = sentAt;
        this.stateFrom = stateFrom;
    }

    public String getCategory() {
        return category;
    }

    public String getAction() {
        return action;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }

    public String getBoxName() {
        return this.boxName;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public Instant getStateFrom() {
        return stateFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvroEventDefinition that = (AvroEventDefinition) o;
        return Objects.equals(experimentId, that.experimentId) &&
                Objects.equals(category, that.category) &&
                Objects.equals(action, that.action) &&
                Objects.equals(value, that.value) &&
                Objects.equals(label, that.label) &&
                Objects.equals(boxName, that.boxName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentId, category, action, value, label, boxName);
    }
}
