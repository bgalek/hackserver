package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;

public class ActivityPeriodDeserializer implements Converter<Document, ActivityPeriod> {
    private final DateTimeDeserializer dateTimeDeserializer;

    public ActivityPeriodDeserializer(DateTimeDeserializer dateTimeDeserializer) {
        this.dateTimeDeserializer = dateTimeDeserializer;
    }

    @Override
    public ActivityPeriod convert(Document source) {
        return new ActivityPeriod(dateTimeDeserializer.convert(source.getString("activeFrom")),
                dateTimeDeserializer.convert(source.getString("activeTo")));
    }
}
