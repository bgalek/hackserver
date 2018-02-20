package pl.allegro.experiments.chi.chiserver.domain.experiments;

public class InternalPredicate implements Predicate {
    public InternalPredicate() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
