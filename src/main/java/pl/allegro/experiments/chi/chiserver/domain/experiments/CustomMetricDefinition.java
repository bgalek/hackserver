package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class CustomMetricDefinition {
    private final String name;
    private final EventDefinition viewEventDefinition;
    private final EventDefinition successEventDefinition;

    @JsonCreator
    public CustomMetricDefinition(
            @JsonProperty("name") String name,
            @JsonProperty("viewEventDefinition") EventDefinition viewEventDefinition,
            @JsonProperty("successEventDefinition") EventDefinition successEventDefinition) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(viewEventDefinition);
        Preconditions.checkNotNull(successEventDefinition);
        this.name = name;
        this.viewEventDefinition = viewEventDefinition;
        this.successEventDefinition = successEventDefinition;

    }

    public String getName() { return name; }

    public EventDefinition getViewEventDefinition() { return viewEventDefinition; }

    public EventDefinition getSuccessEventDefinition() { return  successEventDefinition; }
}
