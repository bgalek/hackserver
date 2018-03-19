package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import com.mongodb.DBObject;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ExperimentVariantDeserializer implements Converter<DBObject, ExperimentVariant> {
    private final PredicateDeserializer predicateDeserializer;

    public ExperimentVariantDeserializer(PredicateDeserializer predicateDeserializer) {
        this.predicateDeserializer = predicateDeserializer;
    }

    @Override
    public ExperimentVariant convert(DBObject bson) {
        return new ExperimentVariant(
                (String) bson.get("name"),
                ((List<DBObject>) bson.get("predicates")).stream()
                        .map(it -> predicateDeserializer.convert(it))
                        .collect(Collectors.toList())
        );
    }
}
