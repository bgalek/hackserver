package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;

@ReadingConverter
public class ExperimentTagDeserializer implements Converter<String, ExperimentTag> {

    @Override
    public ExperimentTag convert(String source) {
        return new ExperimentTag(source);
    }
}
