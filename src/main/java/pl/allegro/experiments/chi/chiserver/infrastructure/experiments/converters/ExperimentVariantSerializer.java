package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ExperimentVariantSerializer implements Converter<ExperimentVariant, DBObject> {
    private final PredicateSerializer predicateSerializer;

    public ExperimentVariantSerializer(PredicateSerializer predicateSerializer) {
        this.predicateSerializer = predicateSerializer;
    }

    @Override
    public DBObject convert(ExperimentVariant source) {
        Map<String, Object> experimentVariantAsMap = new HashMap<>();
        experimentVariantAsMap.put("name", source.getName());
        experimentVariantAsMap.put("predicates", source.getPredicates().stream()
                .map(predicateSerializer::convert)
                .collect(Collectors.toList()));
        return new BasicDBObject(experimentVariantAsMap);
    }
}
