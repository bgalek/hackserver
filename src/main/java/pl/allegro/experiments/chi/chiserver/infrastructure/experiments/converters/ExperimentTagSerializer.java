package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;

@WritingConverter
public class ExperimentTagSerializer implements Converter<ExperimentTag, String> {

    @Override
    public String convert(ExperimentTag source) {
        return source.getId();
    }
}
