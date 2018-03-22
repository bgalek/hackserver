package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.core.convert.converter.Converter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeDeserializer implements Converter<String, ZonedDateTime> {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");

    @Override
    public ZonedDateTime convert(String source) {
        return ZonedDateTime.parse(source, dateFormatter);
    }
}
