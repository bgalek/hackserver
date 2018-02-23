package pl.allegro.experiments.chi.chiserver.domain.statistics;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VariantStatistics that = (VariantStatistics) o;

        if (Double.compare(that.value, value) != 0) return false;
        if (Double.compare(that.diff, diff) != 0) return false;
        if (Double.compare(that.pValue, pValue) != 0) return false;
        return count == that.count;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(value);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(diff);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(pValue);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + count;
        return result;
    }
}
