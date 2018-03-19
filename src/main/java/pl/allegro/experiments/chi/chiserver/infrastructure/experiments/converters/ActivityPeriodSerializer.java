package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;

public class ActivityPeriodSerializer implements Converter<ActivityPeriod, BasicDBObject> {
    private final DateTimeSerializer dateTimeSerializer;

    public ActivityPeriodSerializer(DateTimeSerializer dateTimeSerializer) {
        this.dateTimeSerializer = dateTimeSerializer;
    }

    @Override
    public BasicDBObject convert(ActivityPeriod source) {
        BasicDBObject result = new BasicDBObject();
        result.append("activeFrom", dateTimeSerializer.convert(source.getActiveFrom()));
        result.append("activeTo", dateTimeSerializer.convert(source.getActiveTo()));
        return result;
    }
}
