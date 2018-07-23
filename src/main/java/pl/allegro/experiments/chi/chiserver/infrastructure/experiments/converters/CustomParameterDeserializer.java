package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomParameter;

public class CustomParameterDeserializer implements Converter<Document, CustomParameter> {

    @Override
    public CustomParameter convert(Document source) {
        return new CustomParameter(source.getString("name"), source.getString("value"));
    }
}
