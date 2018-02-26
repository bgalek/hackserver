package pl.allegro.experiments.chi.chiserver.domain.experiments;

public class ExperimentMeasurements {
    private final int lastDayVisits;

    public ExperimentMeasurements() {
        this(0);
    }

    public ExperimentMeasurements(int lastDayVisits) {
        this.lastDayVisits = lastDayVisits;
    }

    public int getLastDayVisits() {
        return lastDayVisits;
    }

}