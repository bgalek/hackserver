package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ExperimentDeserializer implements Converter<DBObject, Experiment> {
    private final ExperimentVariantDeserializer experimentVariantDeserializer;
    private final ActivityPeriodDeserializer activityPeriodDeserializer;

    public ExperimentDeserializer(
            ExperimentVariantDeserializer experimentVariantDeserializer,
            ActivityPeriodDeserializer activityPeriodDeserializer) {
        this.experimentVariantDeserializer = experimentVariantDeserializer;
        this.activityPeriodDeserializer = activityPeriodDeserializer;
    }

    @Override
    public Experiment convert(DBObject bson) {
        String id = (String) bson.get("_id");
        List<DBObject> rawVariants = (List<DBObject>) bson.get("variants");
        List<ExperimentVariant> variants = rawVariants.stream()
                .map(experimentVariantDeserializer::convert)
                .collect(Collectors.toList());
        String description = bson.get("description") != null ? (String) bson.get("description") : null;
        String documentLink = bson.get("documentLink") != null ? (String) bson.get("documentLink") : null;
        String author = bson.get("author") != null ? (String) bson.get("author") : null;
        List<String> groups = bson.get("groups") != null ? (List<String>) bson.get("groups") : null;
        boolean reportingEnabled = bson.get("reportingEnabled") != null ? (boolean) bson.get("reportingEnabled") : null;
        Object rawActivityPeriod = bson.get("activityPeriod");
        ActivityPeriod activityPeriod = rawActivityPeriod != null ? activityPeriodDeserializer.convert((BasicDBObject) rawActivityPeriod) : null;
        Object rawExplicitStatus = bson.get("explicitStatus");
        ExperimentStatus explicitStatus = rawExplicitStatus != null ? ExperimentStatus.valueOf((String) rawExplicitStatus) : null;
        return Experiment.builder()
                .id(id)
                .variants(variants)
                .description(description)
                .documentLink(documentLink)
                .author(author)
                .groups(groups)
                .reportingEnabled(reportingEnabled)
                .activityPeriod(activityPeriod)
                .explicitStatus(explicitStatus)
                .build();
    }
}
