package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;

public class MetricName {
    private final String value;

    private MetricName(String value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    public static MetricName of(String value) {
        return new MetricName(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof MetricName)) return false;

        MetricName that = (MetricName) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
