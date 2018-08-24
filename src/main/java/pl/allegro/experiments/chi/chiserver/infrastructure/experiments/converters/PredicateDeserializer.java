package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.google.common.base.Preconditions;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PredicateDeserializer implements Converter<Document, Predicate> {
    private final Map<String, Converter<Document, Predicate>> predicateDeserializers = new HashMap<>();

    public PredicateDeserializer() {
        predicateDeserializers.put(PredicateType.HASH.toString(), new HashRangePredicateDeserializer());
        predicateDeserializers.put(PredicateType.INTERNAL.toString(), new InternalPredicateDeserializer());
        predicateDeserializers.put(PredicateType.FULL_ON.toString(), new FullOnPredicateDeserializer());
        predicateDeserializers.put(PredicateType.DEVICE_CLASS.toString(), new DeviceClassPredicateDeserializer());
    }

    @Override
    public Predicate convert(Document bson) {
        Object rawType = bson.get("type");
        if (rawType == null) {
            throw new IllegalArgumentException("No predicate type in: " + bson.toString());
        }
        String type = (String) rawType;
        Preconditions.checkArgument(predicateDeserializers.keySet().contains(type),
                "Unknown predicate type in: " + bson.toString());
        return predicateDeserializers.get(type).convert(bson);
    }

    class HashRangePredicateDeserializer implements Converter<Document, Predicate> {
        @Override
        public Predicate convert(Document bson) {
            return new HashRangePredicate(new PercentageRange((int) bson.get("from"), (int) bson.get("to")));
        }
    }

    class InternalPredicateDeserializer implements Converter<Document, Predicate> {
        @Override
        public Predicate convert(Document source) {
            return new InternalPredicate();
        }
    }

    class FullOnPredicateDeserializer implements Converter<Document, Predicate> {
        @Override
        public Predicate convert(Document source) {
            return new FullOnPredicate();
        }
    }

    class DeviceClassPredicateDeserializer implements Converter<Document, Predicate> {
        @Override
        public Predicate convert(Document bson) {
            return new DeviceClassPredicate((String) bson.get("device"));
        }
    }
}
