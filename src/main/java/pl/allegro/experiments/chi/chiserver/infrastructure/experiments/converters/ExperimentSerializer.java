package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus;

import java.util.HashMap;
import java.util.Map;

public class ExperimentSerializer implements Converter<ExperimentDefinition, Document> {
    private final DateTimeSerializer dateTimeSerializer;
    private final ReportingDefinitionSerializer reportingDefinitionSerializer;
    private final CustomParameterSerializer customParameterSerializer;

    public ExperimentSerializer(
            DateTimeSerializer dateTimeSerializer,
            ReportingDefinitionSerializer reportingDefinitionSerializer,
            CustomParameterSerializer customParameterSerializer) {
        this.dateTimeSerializer = dateTimeSerializer;
        this.reportingDefinitionSerializer = reportingDefinitionSerializer;
        this.customParameterSerializer = customParameterSerializer;
    }

    @Override
    public Document convert(ExperimentDefinition source) {
        Document result = new Document();

        result.put("_id", source.getId());
        result.put("variantNames", source.getVariantNames());
        result.put("internalVariantName", source.getInternalVariantName().orElse(null));
        result.put("deviceClass", source.getDeviceClass().toJsonString());
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
        result.put("reportingEnabled", source.isReportingEnabled());
        if (source.getActivityPeriod() != null) {
            Map<String, String> activityPeriodAsMap = new HashMap<>();
            activityPeriodAsMap.put("activeFrom", dateTimeSerializer.convert(source.getActivityPeriod().getActiveFrom()));
            activityPeriodAsMap.put("activeTo", dateTimeSerializer.convert(source.getActivityPeriod().getActiveTo()));
            result.put("activityPeriod", activityPeriodAsMap);
        }
        ExperimentStatus experimentStatus = source.getStatus().explicitOrNull();
        result.put("explicitStatus", experimentStatus != null ? experimentStatus.toString() : null);
        result.put("reportingDefinition", reportingDefinitionSerializer.convert(source.getReportingDefinitionToSave()));
        if (source.getCustomParameter() != null) {
            result.put("customParameter", customParameterSerializer.convert(source.getCustomParameter()));
        }
        return result;
    }
}