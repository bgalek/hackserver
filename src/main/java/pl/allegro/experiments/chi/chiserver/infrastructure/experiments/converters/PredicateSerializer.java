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
        BasicDBObject result = new BasicDBObject();

        if (source instanceof HashRangePredicate) {
            HashRangePredicate predicate = (HashRangePredicate) source;
            result.put("type", PredicateType.HASH.toString());
            result.put("from", predicate.getHashRange().getFrom());
            result.put("to", predicate.getHashRange().getTo());
        } else if (source instanceof InternalPredicate) {
            result.put("type", PredicateType.INTERNAL.toString());
        } else if (source instanceof CmuidRegexpPredicate) {
            CmuidRegexpPredicate predicate = (CmuidRegexpPredicate) source;
            result.put("type", PredicateType.CMUID_REGEXP.toString());
            result.put("regexp", predicate.getPattern().toString());
        } else if (source instanceof DeviceClassPredicate) {
            DeviceClassPredicate predicate = (DeviceClassPredicate) source;
            result.put("type", PredicateType.DEVICE_CLASS.toString());
            result.put("device", predicate.getDevice());
        } else {
            throw new UnsupportedOperationException("Can't serialize " + source.getClass().getName());
        }

        return new BasicDBObject(result);
    }
}
