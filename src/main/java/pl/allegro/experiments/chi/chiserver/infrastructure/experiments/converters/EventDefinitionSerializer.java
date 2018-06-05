package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;

public class EventDefinitionSerializer implements Converter<EventDefinition, Document> {

    @Override
    public Document convert(EventDefinition source) {
        Document result = new Document();

        result.put("category", source.getCategory());
        result.put("value", source.getValue());
        result.put("action", source.getAction());
        result.put("label", source.getLabel());
        result.put("boxName", source.getBoxName());

        return result;
    }
}
