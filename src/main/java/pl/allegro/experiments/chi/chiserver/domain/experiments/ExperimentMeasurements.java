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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExperimentMeasurements that = (ExperimentMeasurements) o;

        return lastDayVisits == that.lastDayVisits;
    }

    @Override
    public int hashCode() {
        return lastDayVisits;
    }
}