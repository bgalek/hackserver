package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.CmuidRegexpPredicateSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.*;
import java.util.regex.Pattern;

public class PredicateDeserializer implements Converter<DBObject, Predicate> {
    private final Map<String, Converter<DBObject, Predicate>> predicateDeserializers = new HashMap<>();

    public PredicateDeserializer() {
        predicateDeserializers.put("HASH", new HashRangePredicateDeserializer());
        predicateDeserializers.put("INTERNAL", new InternalPredicateDeserializer());
        predicateDeserializers.put("DEVICE_CLASS", new DeviceClassPredicateDeserializer());
        predicateDeserializers.put("CMUID_REGEXP", new CmuidRegexpPredicateDeserializer());
    }

    @Override
    public Predicate convert(DBObject bson) {
        Object rawType = bson.get("type");
        if (rawType == null) {
            throw new IllegalArgumentException("No predicate type in: " + bson.toString());
        }
        String type = (String) rawType;
        Preconditions.checkArgument(predicateDeserializers.keySet().contains(type),
                "Unknown predicate type in: " + bson.toString());
        return predicateDeserializers.get(type).convert(bson);
    }

    class HashRangePredicateDeserializer implements Converter<DBObject, Predicate> {
        @Override
        public Predicate convert(DBObject bson) {
            return new HashRangePredicate(new PercentageRange((int) bson.get("from"), (int) bson.get("to")));
        }
    }

    class InternalPredicateDeserializer implements Converter<DBObject, Predicate> {
        @Override
        public Predicate convert(DBObject source) {
            return new InternalPredicate();
        }
    }

    class DeviceClassPredicateDeserializer implements Converter<DBObject, Predicate> {
        @Override
        public Predicate convert(DBObject bson) {
            return new DeviceClassPredicate((String) bson.get("device"));
        }
    }

    class CmuidRegexpPredicateDeserializer implements Converter<DBObject, Predicate> {
        @Override
        public Predicate convert(DBObject bson) {
            return new CmuidRegexpPredicate(Pattern.compile((String) bson.get("regexp")));
        }
    }
}
