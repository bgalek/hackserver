package pl.allegro.experiments.chi.chiserver.domain.experiments;

import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "experimentTags")
public class ExperimentTag {

    @org.springframework.data.annotation.Id
    private final String id;

    public ExperimentTag(String id) {
        this.id = id;
    }

    @Id
    @DiffInclude
    public String getId() {
        return id;
    }
}
