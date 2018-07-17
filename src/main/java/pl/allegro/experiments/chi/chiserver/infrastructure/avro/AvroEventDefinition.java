package pl.allegro.experiments.chi.chiserver.infrastructure.avro;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class AvroEventDefinition {
    private final String experimentId;
    private final String category;
    private final String action;
    private final String value;
    private final String label;
    private final String boxName;
    private final Instant sentAt;
    private final Instant __timestamp; // state from

    @JsonCreator
    public AvroEventDefinition(
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("category") String category,
            @JsonProperty("action") String action,
            @JsonProperty("value") String value,
            @JsonProperty("label") String label,
            @JsonProperty("boxName") String boxName,
            @JsonProperty("sentAt") Instant sentAt,
            @JsonProperty("__timestamp") Instant __timestamp
            ) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(sentAt);
        Preconditions.checkNotNull(__timestamp);
        Preconditions.checkArgument(experimentId.length() > 0);

        this.experimentId = experimentId;
        this.category = Optional.ofNullable(category).orElse("");
        this.action = Optional.ofNullable(action).orElse("");
        this.value = Optional.ofNullable(value).orElse("");
        this.label = Optional.ofNullable(label).orElse("");
        this.boxName = Optional.ofNullable(boxName).orElse("");
        this.sentAt = sentAt.truncatedTo(ChronoUnit.SECONDS);
        this.__timestamp = __timestamp.truncatedTo(ChronoUnit.SECONDS);
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

    public Instant get__timestamp() {
        return __timestamp;
    }

    public static List<AvroEventDefinition> listFrom(List<ExperimentDefinition> experimentDefinitions) {
        Instant now = Instant.now();
        List<AvroEventDefinition> result = new ArrayList<>();
        experimentDefinitions.forEach(experimentDefinition ->
                result.addAll(AvroEventDefinition.listFrom(experimentDefinition, now)));
        return result;
    }

    private static List<AvroEventDefinition> listFrom(ExperimentDefinition experimentDefinition, Instant now) {
        return experimentDefinition.getReportingDefinition().getEventDefinitions()
                .stream()
                .map(eventDefinition -> new AvroEventDefinition(
                        experimentDefinition.getId(),
                        eventDefinition.getCategory(),
                        eventDefinition.getAction(),
                        eventDefinition.getValue(),
                        eventDefinition.getLabel(),
                        eventDefinition.getBoxName(),
                        now, now.truncatedTo(DAYS)))
                .collect(Collectors.toList());
    }
}
