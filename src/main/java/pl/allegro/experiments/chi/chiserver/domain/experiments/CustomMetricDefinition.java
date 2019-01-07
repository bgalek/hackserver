package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import java.util.List;

public class CustomMetricDefinition {
    private final String metricName;
    private final List<EventDefinitionForVariant> definitionForVariants;

    @JsonCreator
    public CustomMetricDefinition(
            @JsonProperty("metricName") String metricName,
            @JsonProperty("definitionForVariants") List<EventDefinitionForVariant> definitionForVariants) {
        Preconditions.checkNotNull(metricName);
        Preconditions.checkNotNull(definitionForVariants);
        this.metricName = metricName;
        this.definitionForVariants = definitionForVariants;

    }

    public String getName() { return metricName; }

    public List<EventDefinitionForVariant> getDefinitionForVariants() { return  definitionForVariants; }
}
