package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition;

import java.util.List;
import java.util.stream.Collectors;

public class ReportingDefinitionDeserializer implements Converter<DBObject, ReportingDefinition> {
    private final EventDefinitionDeserializer eventDefinitionDeserializer;

    public ReportingDefinitionDeserializer(EventDefinitionDeserializer eventDefinitionDeserializer) {
        this.eventDefinitionDeserializer = eventDefinitionDeserializer;
    }

    @Override
    public ReportingDefinition convert(DBObject source) {
        if (source.get("eventDefinitions") != null) {
            return new ReportingDefinition(
                    ((List<DBObject>) source.get("eventDefinitions")).stream()
                            .map(eventDefinitionDeserializer::convert)
                            .collect(Collectors.toList()),
                    (boolean) source.get("gtm"),
                    (boolean) source.get("backendInteractionsEnabled")
            );
        } else {
            return new ReportingDefinition(
                    null,
                    (boolean) source.get("gtm"),
                    (boolean) source.get("backendInteractionsEnabled")
            );
        }
    }
}