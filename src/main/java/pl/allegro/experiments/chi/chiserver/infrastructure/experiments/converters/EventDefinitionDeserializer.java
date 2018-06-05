package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;

public class EventDefinitionDeserializer implements Converter<Document, EventDefinition> {

    @Override
    public EventDefinition convert(Document source) {
        return new EventDefinition(
                (String) source.get("category"),
                (String) source.get("action"),
                (String) source.get("value"),
                (String) source.get("label"),
                (String) source.get("boxName")
        );
    }
}