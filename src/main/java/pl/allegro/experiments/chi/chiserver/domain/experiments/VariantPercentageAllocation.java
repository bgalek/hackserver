package pl.allegro.experiments.chi.chiserver.domain.experiments;

public class VariantPercentageAllocation {
    private final String variantName;
    private final PercentageRange range;


    public VariantPercentageAllocation(String variantName, PercentageRange range) {
        this.variantName = variantName;
        this.range = range;
    }

    public String getVariantName() {
        return variantName;
    }

    public PercentageRange getRange() {
        return range;
    }
}


