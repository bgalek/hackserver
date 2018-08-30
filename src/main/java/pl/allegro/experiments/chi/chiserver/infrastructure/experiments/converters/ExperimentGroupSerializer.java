package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters;

import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;

@Service
public class ExperimentGroupSerializer implements Converter<ExperimentGroup, Document> {

    @Override
    public Document convert(ExperimentGroup source) {
        Document result = new Document();

        result.put("experiments", source.getExperiments());
        result.put("id", source.getId());
        result.put("salt", source.getSalt());

        return result;
    }
}
