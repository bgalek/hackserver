package pl.allegro.experiments.chi.chiserver.domain.statistics.classic;

public class VariantStatistics {
    private final double value;
    private final double diff;
    private final double pValue;
    private final int count;

    public VariantStatistics(
            double value,
            double diff,
            double pValue,
            int count) {
        this.value = value;
        this.diff = diff;
        this.pValue = pValue;
        this.count = count;
    }

    public double getValue() {
        return value;
    }

    public double getDiff() {
        return diff;
    }

    public double getpValue() {
        return pValue;
    }

    public int getCount() {
        return count;
    }
}
