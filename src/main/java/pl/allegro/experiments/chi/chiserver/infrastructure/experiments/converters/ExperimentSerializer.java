package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentSerializer implements Converter<ExperimentDefinition, DBObject> {
    private final DateTimeSerializer dateTimeSerializer;
    private final ReportingDefinitionSerializer reportingDefinitionSerializer;

    public ExperimentSerializer(
            DateTimeSerializer dateTimeSerializer,
            ReportingDefinitionSerializer reportingDefinitionSerializer) {
        this.dateTimeSerializer = dateTimeSerializer;
        this.reportingDefinitionSerializer = reportingDefinitionSerializer;
    }

    @Override
    public DBObject convert(ExperimentDefinition source) {
        BasicDBObject result = new BasicDBObject();

        result.put("_id", source.getId());
        result.put("variantNames", source.getVariantNames());
        result.put("internalVariantName", source.getInternalVariantName().orElse(null));
        result.put("deviceClass", source.getDeviceClass().orElse(null));
        result.put("percentage", source.getPercentage().orElse(null));

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
        result.put("reportingDefinition", reportingDefinitionSerializer.convert(source.getReportingDefinition()));
        return result;
    }
}