package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

@WritingConverter
public class DeviceClassSerializer implements Converter<DeviceClass, String> {
    @Override
    public String convert(DeviceClass source) {
        return source.toBsonString();
    }
}
