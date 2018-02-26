package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;

public class VariantName {
    private final String value;

    private VariantName(String value) {
        Preconditions.checkNotNull(value);
        this.value = value;
    }

    public static VariantName of(String value) {
        return new VariantName(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof VariantName)) return false;

        VariantName that = (VariantName) o;

        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
