package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;

import java.util.List;

public class ExperimentGroupDeserializer implements Converter<Document, ExperimentGroup> {

    @Override
    public ExperimentGroup convert(Document source) {
        return new ExperimentGroup(
                (String) source.get("id"),
                (String) source.get("salt"),
                (List<String>) source.get("experiments")
        );
    }
}
