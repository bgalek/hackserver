package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.List;

public class CustomMetricDefinition {
    private final String metricName;
    private final List<EventDefinitionForVariant> definitionForVariant;

    @JsonCreator
    public CustomMetricDefinition(
            @JsonProperty("metricName") String metricName,
            @JsonProperty("definitionForVariant") List<EventDefinitionForVariant> definitionForVariant) {
        Preconditions.checkNotNull(metricName);
        Preconditions.checkNotNull(definitionForVariant);
        this.metricName = metricName;
        this.definitionForVariant = definitionForVariant;

    }

    public String getName() { return metricName; }

    public List<EventDefinitionForVariant> getDefinitionForVariant() { return  definitionForVariant; }
}
