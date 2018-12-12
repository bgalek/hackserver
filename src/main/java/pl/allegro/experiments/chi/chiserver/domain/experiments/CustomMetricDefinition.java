package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

public class CustomMetricDefinition {
    private final String name;
    private final EventDefinition viewEventDefinition;
    private final EventDefinition successEventDefinition;

    public CustomMetricDefinition(
            String name,
            EventDefinition viewEventDefinition,
            EventDefinition successEventDefinition) {
        this.name = name;
        this.viewEventDefinition = viewEventDefinition;
        this.successEventDefinition = successEventDefinition;
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(viewEventDefinition);
        Preconditions.checkNotNull(successEventDefinition);
    }

    public String getName() { return name; }

    public EventDefinition getViewEventDefinition() { return viewEventDefinition; }

    public EventDefinition getSuccessEventDefinition() { return  successEventDefinition; }
}
