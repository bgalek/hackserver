package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kristofa.brave.internal.zipkin.internal.moshi.Json;
import com.google.common.base.Preconditions;

public class CustomMetricDefinition {
    private final String name;
    private final String variant;
    private final EventDefinition viewEventDefinition;
    private final EventDefinition successEventDefinition;

    @JsonCreator
    public CustomMetricDefinition(
            @JsonProperty("name") String name,
            @JsonProperty("variant") String variant,
            @JsonProperty("viewEventDefinition") EventDefinition viewEventDefinition,
            @JsonProperty("successEventDefinition") EventDefinition successEventDefinition) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(variant);
        Preconditions.checkNotNull(viewEventDefinition);
        Preconditions.checkNotNull(successEventDefinition);
        this.name = name;
        this.variant = variant;
        this.viewEventDefinition = viewEventDefinition;
        this.successEventDefinition = successEventDefinition;

    }

    public String getName() { return name; }

    public String getVariant() { return variant; }

    public EventDefinition getViewEventDefinition() { return viewEventDefinition; }

    public EventDefinition getSuccessEventDefinition() { return  successEventDefinition; }
}
