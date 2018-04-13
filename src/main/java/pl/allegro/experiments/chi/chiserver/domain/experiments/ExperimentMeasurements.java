package pl.allegro.experiments.chi.chiserver.domain.experiments;

public class ExperimentMeasurements {
    private final int lastDayVisits;

    public ExperimentMeasurements(Integer lastDayVisits) {
        if (lastDayVisits == null) {
            this.lastDayVisits = 0;
        } else {
            this.lastDayVisits = lastDayVisits;
        }
    }

    public int getLastDayVisits() {
        return lastDayVisits;
    }

}