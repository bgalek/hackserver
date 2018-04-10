package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;

public class EventDefinitionDeserializer implements Converter<DBObject, EventDefinition> {

    @Override
    public EventDefinition convert(DBObject source) {
        return new EventDefinition(
                (String) source.get("category"),
                (String) source.get("action"),
                (String) source.get("value"),
                (String) source.get("label")
        );
    }
}
