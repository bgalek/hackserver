package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WritingConverter
public class DateTimeSerializer implements Converter<ZonedDateTime, String> {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public String convert(ZonedDateTime source) {
        return source.format(dateFormatter);
    }
}
