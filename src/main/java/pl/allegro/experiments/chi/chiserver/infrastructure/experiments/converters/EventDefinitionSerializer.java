package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;

public class EventDefinitionSerializer implements Converter<EventDefinition, DBObject> {
    @Override
    public DBObject convert(EventDefinition source) {
        BasicDBObject result = new BasicDBObject();

        result.put("category", source.getCategory());
        result.put("value", source.getValue());
        result.put("action", source.getAction());
        result.put("label", source.getLabel());
        result.put("boxName", source.getBoxName());

        return result;
    }
}
