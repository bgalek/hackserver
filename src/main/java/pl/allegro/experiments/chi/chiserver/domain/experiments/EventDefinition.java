package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class EventDefinition {
    private final String category;
    private final String action;
    private final String value;
    private final String label;

    @JsonCreator
    public EventDefinition(
            @JsonProperty("category") String category,
            @JsonProperty("action") String action,
            @JsonProperty("value") String value,
            @JsonProperty("label") String label) {
        this.category = Optional.ofNullable(category).orElse("");
        this.action = Optional.ofNullable(action).orElse("");
        this.value = Optional.ofNullable(value).orElse("");
        this.label = Optional.ofNullable(label).orElse("");
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
}
