package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod;

public class ActivityPeriodSerializer implements Converter<ActivityPeriod, Document> {
    private final DateTimeSerializer dateTimeSerializer;

    public ActivityPeriodSerializer(DateTimeSerializer dateTimeSerializer) {
        this.dateTimeSerializer = dateTimeSerializer;
    }

    @Override
    public Document convert(ActivityPeriod source) {
        Document result = new Document();
        result.append("activeFrom", dateTimeSerializer.convert(source.getActiveFrom()));
        result.append("activeTo", dateTimeSerializer.convert(source.getActiveTo()));
        return result;
    }
}
