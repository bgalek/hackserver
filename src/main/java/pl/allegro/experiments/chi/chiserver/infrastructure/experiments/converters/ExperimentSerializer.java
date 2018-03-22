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
        BasicDBObject result = new BasicDBObject();

        result.put("_id", source.getId());
        result.put("variants", source.getVariants().stream()
                .map(experimentVariantSerializer::convert)
                .collect(Collectors.toList()));
        if (source.getDescription() != null) {
            result.put("description", source.getDescription());
        }
        if (source.getDocumentLink() != null) {
            result.put("documentLink", source.getDocumentLink());
        }
        if (source.getAuthor() != null) {
            result.put("author", source.getAuthor());
        }
        result.put("groups", source.getGroups());
        result.put("reportingEnabled", source.getReportingEnabled());
        if (source.getActivityPeriod() != null) {
            Map<String, String> activityPeriodAsMap = new HashMap<>();
            activityPeriodAsMap.put("activeFrom", dateTimeSerializer.convert(source.getActivityPeriod().getActiveFrom()));
            activityPeriodAsMap.put("activeTo", dateTimeSerializer.convert(source.getActivityPeriod().getActiveTo()));
            result.put("activityPeriod", activityPeriodAsMap);
        }
        ExperimentStatus experimentStatus = source.getStatus().explicitOrNull();
        result.put("explicitStatus", experimentStatus != null ? experimentStatus.toString() : null);
        return result;
    }
}