package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import joptsimple.internal.Strings;

import java.util.List;

public class ExperimentVariant {
    private final String name;
    private final List<Predicate> predicates;

    public ExperimentVariant(String name, List<Predicate> predicates) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(name));
        Preconditions.checkNotNull(predicates);
        this.name = name;
        this.predicates = predicates;
    }

    public String getName() {
        return name;
    }

    public List<Predicate> getPredicates() {
        return predicates;
    }

}
