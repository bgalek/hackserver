package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition;

import java.util.stream.Collectors;

public class ReportingDefinitionSerializer implements Converter<ReportingDefinition, Document> {
    private final EventDefinitionSerializer eventDefinitionSerializer;

    public ReportingDefinitionSerializer(EventDefinitionSerializer eventDefinitionSerializer) {
        this.eventDefinitionSerializer = eventDefinitionSerializer;
    }

    @Override
    public Document convert(ReportingDefinition source) {
        Document result = new Document();

        result.put("gtm", source.isGtm());
        result.put("backendInteractionsEnabled", source.isBackendInteractionsEnabled());
        result.put("eventDefinitions", source.getEventDefinitions().stream()
                .map(eventDefinitionSerializer::convert)
                .collect(Collectors.toList()));

        return result;
    }
}
