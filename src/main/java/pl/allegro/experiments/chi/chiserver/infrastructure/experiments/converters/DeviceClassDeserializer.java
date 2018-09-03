package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;

@ReadingConverter
public class DeviceClassDeserializer implements Converter<String, DeviceClass> {
    @Override
    public DeviceClass convert(String source) {
        return DeviceClass.fromString(source);
    }
}
