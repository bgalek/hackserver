package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.stream.Collectors;

public class ExperimentVariantSerializer implements Converter<ExperimentVariant, DBObject> {
    private final PredicateSerializer predicateSerializer;

    public ExperimentVariantSerializer(PredicateSerializer predicateSerializer) {
        this.predicateSerializer = predicateSerializer;
    }

    @Override
    public DBObject convert(ExperimentVariant source) {
        BasicDBObject result = new BasicDBObject();
        result.put("name", source.getName());
        result.put("predicates", source.getPredicates().stream()
                .map(predicateSerializer::convert)
                .collect(Collectors.toList()));
        return result;
    }
}
