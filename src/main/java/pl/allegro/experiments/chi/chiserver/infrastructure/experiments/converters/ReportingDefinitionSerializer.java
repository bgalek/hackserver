package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition;

import java.util.stream.Collectors;

public class ReportingDefinitionSerializer implements Converter<ReportingDefinition, DBObject> {
    private final EventDefinitionSerializer eventDefinitionSerializer;

    public ReportingDefinitionSerializer(EventDefinitionSerializer eventDefinitionSerializer) {
        this.eventDefinitionSerializer = eventDefinitionSerializer;
    }

    @Override
    public DBObject convert(ReportingDefinition source) {
        BasicDBObject result = new BasicDBObject();

        result.put("gtm", source.isGtm());
        result.put("backendInteractionsEnabled", source.isBackendInteractionsEnabled());
        result.put("eventDefinitions", source.getEventDefinitions().stream()
                .map(eventDefinitionSerializer::convert)
                .collect(Collectors.toList()));
        return result;
    }
}
