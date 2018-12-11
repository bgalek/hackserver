package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "customMetricDefinitions")
@TypeName("customMetric")
public class CustomMetricDefinition {
    private final String name;
    private final EventDefinition viewEventDefinition;
    private final EventDefinition clickEventDefinition;

    CustomMetricDefinition(
            String name,
            EventDefinition viewEventDefinition,
            EventDefinition clickEventDefinition) {
        this.name = name;
        this.viewEventDefinition = viewEventDefinition;
        this.clickEventDefinition = clickEventDefinition;
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(viewEventDefinition);
        Preconditions.checkNotNull(clickEventDefinition);
    }

    public String getName() { return name; }

    public EventDefinition getViewEventDefinition() { return viewEventDefinition; }

    public EventDefinition getClickEventDefinition() { return  clickEventDefinition; }
}
