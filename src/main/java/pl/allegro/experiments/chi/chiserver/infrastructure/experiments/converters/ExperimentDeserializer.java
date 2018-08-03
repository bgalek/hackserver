package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.List;
import java.util.Optional;

public class ExperimentDeserializer implements Converter<Document, ExperimentDefinition> {
    private final ActivityPeriodDeserializer activityPeriodDeserializer;
    private final ReportingDefinitionDeserializer reportingDefinitionDeserializer;
    private final CustomParameterDeserializer customParameterDeserializer;

    public ExperimentDeserializer(
            ActivityPeriodDeserializer activityPeriodDeserializer,
            ReportingDefinitionDeserializer reportingDefinitionDeserializer,
            CustomParameterDeserializer customParameterDeserializer) {
        this.activityPeriodDeserializer = activityPeriodDeserializer;
        this.reportingDefinitionDeserializer = reportingDefinitionDeserializer;
        this.customParameterDeserializer = customParameterDeserializer;
    }

    @Override
    public ExperimentDefinition convert(Document bson) {
        String id = (String) bson.get("_id");
        List<String> variantNames = (List<String>) bson.get("variantNames");
        String internalVariantName = (String)bson.get("internalVariantName");
        String fullOnVariantName = (String)bson.get("fullOnVariantName");
        String deviceClass = (String)bson.get("deviceClass");
        Integer percentage = (Integer)bson.get("percentage");
        String description = bson.get("description") != null ? (String) bson.get("description") : null;
        String documentLink = bson.get("documentLink") != null ? (String) bson.get("documentLink") : null;
        String author = bson.get("author") != null ? (String) bson.get("author") : null;
        List<String> groups = bson.get("groups") != null ? (List<String>) bson.get("groups") : null;
        boolean reportingEnabled = bson.get("reportingEnabled") != null ? (boolean) bson.get("reportingEnabled") : null; // wroc
        ActivityPeriod activityPeriod = Optional.ofNullable(bson.get("activityPeriod"))
                .map(a -> activityPeriodDeserializer.convert((Document)a))
                .orElse(null);
        Object rawExplicitStatus = bson.get("explicitStatus");
        ExperimentStatus explicitStatus = rawExplicitStatus != null ? ExperimentStatus.valueOf((String) rawExplicitStatus) : null;

        ExperimentDefinition.Builder result = ExperimentDefinition.builder()
                .id(id)
                .variantNames(variantNames)
                .internalVariantName(internalVariantName)
                .fullOnVariantName(fullOnVariantName)
                .percentage(percentage)
                .deviceClass(deviceClass)
                .description(description)
                .documentLink(documentLink)
                .author(author)
                .groups(groups)
                .reportingEnabled(reportingEnabled)
                .activityPeriod(activityPeriod)
                .explicitStatus(explicitStatus);
        if (bson.get("reportingDefinition") != null) {
            result.reportingDefinition(reportingDefinitionDeserializer.convert((Document) bson.get("reportingDefinition")));
        }
        if (bson.get("customParameter") != null) {
            result.customParameter(customParameterDeserializer.convert((Document) bson.get("customParameter")));
        }
        return result.build();
    }
}
