package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;

public class ActivityPeriodDeserializer implements Converter<BasicDBObject, ActivityPeriod> {
    private final DateTimeDeserializer dateTimeDeserializer;

    public ActivityPeriodDeserializer(DateTimeDeserializer dateTimeDeserializer) {
        this.dateTimeDeserializer = dateTimeDeserializer;
    }

    @Override
    public ActivityPeriod convert(BasicDBObject source) {
        String from = source.getString("activeFrom");
        String to = source.getString("activeTo");
        return new ActivityPeriod(dateTimeDeserializer.convert(from),
                dateTimeDeserializer.convert(to));
    }
}
