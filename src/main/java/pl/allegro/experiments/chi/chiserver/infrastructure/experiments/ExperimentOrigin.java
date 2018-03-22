package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

public enum ExperimentOrigin {
    STASH, MONGO, UNDEFINED;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
