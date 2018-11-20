package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "experimentTags")
public class ExperimentTag {

    @org.springframework.data.annotation.Id
    private final String id;

    @PersistenceConstructor
    public ExperimentTag(String id) {
        Preconditions.checkArgument(id != null);
        this.id = id;
    }

    @DiffInclude
    public String getId() {
        return id;
    }
}
