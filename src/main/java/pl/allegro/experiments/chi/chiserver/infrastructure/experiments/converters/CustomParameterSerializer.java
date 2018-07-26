package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomParameter;

public class CustomParameterSerializer implements Converter<CustomParameter, Document> {

    @Override
    public Document convert(CustomParameter source) {
        Document result = new Document();
        if (source.getName() != null) {
            result.put("name", source.getName());
        }
        if (source.getValue() != null) {
            result.put("value", source.getValue());
        }
        return result;
    }
}