package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class EventDefinitionForVariant {
    private final String variantName;
    private final EventDefinition viewEventDefinition;
    private final EventDefinition successEventDefinition;

    @JsonCreator
    public EventDefinitionForVariant(
            @JsonProperty("variantName") String variantName,
            @JsonProperty("viewEventDefinition") EventDefinition viewEventDefinition,
            @JsonProperty("successEventDefinition") EventDefinition successEventDefinition) {
        Preconditions.checkNotNull(variantName);
        Preconditions.checkNotNull(viewEventDefinition);
        Preconditions.checkNotNull(successEventDefinition);
        this.variantName = variantName;
        this.viewEventDefinition = viewEventDefinition;
        this.successEventDefinition = successEventDefinition;
    }

    public String getVariantName() { return variantName; }

    public EventDefinition getViewEventDefinition() { return viewEventDefinition; }

    public EventDefinition getSuccessEventDefinition() { return successEventDefinition; }
}
