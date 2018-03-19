package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.HashMap;
import java.util.Map;

public class PredicateSerializer implements Converter<Predicate, DBObject> {

    @Override
    public DBObject convert(Predicate source) {
        Map<String, Object> predicateAsMap = new HashMap<>();

        if (source instanceof HashRangePredicate) {
            HashRangePredicate predicate = (HashRangePredicate) source;
            predicateAsMap.put("type", "HASH");
            predicateAsMap.put("from", predicate.getHashRange().getFrom());
            predicateAsMap.put("to", predicate.getHashRange().getTo());
        } else if (source instanceof InternalPredicate) {
            predicateAsMap.put("type", "INTERNAL");
        } else if (source instanceof CmuidRegexpPredicate) {
            CmuidRegexpPredicate predicate = (CmuidRegexpPredicate) source;
            predicateAsMap.put("type", "CMUID_REGEXP");
            predicateAsMap.put("regexp", predicate.getPattern().toString());
        } else if (source instanceof DeviceClassPredicate) {
            DeviceClassPredicate predicate = (DeviceClassPredicate) source;
            predicateAsMap.put("type", "DEVICE_CLASS");
            predicateAsMap.put("device", predicate.getDevice());
        } else {
            throw new UnsupportedOperationException("Can't serialize " + source.getClass().getName());
        }

        return new BasicDBObject(predicateAsMap);
    }
}
