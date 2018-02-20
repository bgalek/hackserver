package pl.allegro.experiments.chi.chiserver.domain.experiments;

public interface ExperimentsRepository extends ReadOnlyExperimentsRepository {
    void delete(String experimentId);

    void save(Experiment experiment);

    default String getOrigin(String experimentId) {
        return "undefined";
    }
}