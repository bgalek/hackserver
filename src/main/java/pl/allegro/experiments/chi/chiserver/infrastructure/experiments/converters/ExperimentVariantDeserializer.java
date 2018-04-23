package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.List;
import java.util.stream.Collectors;

public class ExperimentVariantDeserializer implements Converter<Document, ExperimentVariant> {
    private final PredicateDeserializer predicateDeserializer;

    public ExperimentVariantDeserializer(PredicateDeserializer predicateDeserializer) {
        this.predicateDeserializer = predicateDeserializer;
    }

    @Override
    public ExperimentVariant convert(Document bson) {
        return new ExperimentVariant(
                (String) bson.get("name"),
                ((List<Document>) bson.get("predicates")).stream()
                        .map(predicateDeserializer::convert)
                        .collect(Collectors.toList())
        );
    }
}
