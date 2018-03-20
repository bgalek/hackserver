package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentSerializer implements Converter<Experiment, DBObject> {
    private final ExperimentVariantSerializer experimentVariantSerializer;
    private final DateTimeSerializer dateTimeSerializer;

    public ExperimentSerializer(
            ExperimentVariantSerializer experimentVariantSerializer,
            DateTimeSerializer dateTimeSerializer) {
        this.experimentVariantSerializer = experimentVariantSerializer;
        this.dateTimeSerializer = dateTimeSerializer;
    }

    @Override
    public DBObject convert(Experiment source) {
        Map<String, Object> experimentAsJson = new HashMap<>();
        experimentAsJson.put("_id", source.getId());
        experimentAsJson.put("variants", source.getVariants().stream()
                .map(experimentVariantSerializer::convert)
                .collect(Collectors.toList()));
        if (source.getDescription() != null) {
            experimentAsJson.put("description", source.getDescription());
        }
        if (source.getDocumentLink() != null) {
            experimentAsJson.put("documentLink", source.getDocumentLink());
        }
        if (source.getAuthor() != null) {
            experimentAsJson.put("author", source.getAuthor());
        }
        experimentAsJson.put("groups", source.getGroups());
        experimentAsJson.put("reportingEnabled", source.getReportingEnabled());
        if (source.getActivityPeriod() != null) {
            Map<String, String> activityPeriodAsMap = new HashMap<>();
            activityPeriodAsMap.put("activeFrom", dateTimeSerializer.convert(source.getActivityPeriod().getActiveFrom()));
            activityPeriodAsMap.put("activeTo", dateTimeSerializer.convert(source.getActivityPeriod().getActiveTo()));
            experimentAsJson.put("activityPeriod", activityPeriodAsMap);
        }
        ExperimentStatus experimentStatus = source.getStatus().explicitOrNull();
        experimentAsJson.put("explicitStatus", experimentStatus != null ? experimentStatus.toString() : null);
        return new BasicDBObject(experimentAsJson);
    }
}
