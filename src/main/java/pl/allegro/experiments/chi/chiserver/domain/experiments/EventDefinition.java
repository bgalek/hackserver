package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.Optional;

public class EventDefinition {
    private final String category;
    private final String action;
    private final String value;
    private final String label;
    private final String boxName;
    private final boolean isCustom;

    @JsonCreator
    public EventDefinition(
            @JsonProperty("category") String category,
            @JsonProperty("action") String action,
            @JsonProperty("value") String value,
            @JsonProperty("label") String label,
            @JsonProperty("boxName") String boxName,
            @JsonProperty("isCustom") boolean isCustom) {
        this.category = Optional.ofNullable(category).orElse("");
        this.action = Optional.ofNullable(action).orElse("");
        this.value = Optional.ofNullable(value).orElse("");
        this.label = Optional.ofNullable(label).orElse("");
        this.boxName = Optional.ofNullable(boxName).orElse("");
        this.isCustom = Optional.ofNullable(isCustom).orElse(false);
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

    public boolean isCustom() { return this.isCustom; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDefinition that = (EventDefinition) o;
        return  Objects.equals(category, that.category) &&
                Objects.equals(action, that.action) &&
                Objects.equals(value, that.value) &&
                Objects.equals(label, that.label) &&
                Objects.equals(boxName, that.boxName) &&
                Objects.equals(isCustom, that.isCustom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, action, value, label, boxName, isCustom);
    }

    @Override
    public String toString() {
        return "category: " + category + ", action: " + action + ", value: " + value + ", label: " + label + ", boxName: " + boxName + ", isCustom: " +
                 isCustom;
    }
}
