package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant;

import java.util.stream.Collectors;

public class ExperimentVariantSerializer implements Converter<ExperimentVariant, Document> {
    private final PredicateSerializer predicateSerializer;

    public ExperimentVariantSerializer(PredicateSerializer predicateSerializer) {
        this.predicateSerializer = predicateSerializer;
    }

    @Override
    public Document convert(ExperimentVariant source) {
        Document result = new Document();
        result.put("name", source.getName());
        result.put("predicates", source.getPredicates().stream()
                .map(predicateSerializer::convert)
                .collect(Collectors.toList()));
        return result;
    }
}
