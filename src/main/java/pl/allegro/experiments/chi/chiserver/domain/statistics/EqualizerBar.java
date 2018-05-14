package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;

public class EqualizerBar {
    private final String variantName; //nullable
    private final List<Double> improveProbabilities;
    private final List<Double> impairProbabilities;

    public EqualizerBar(String variantName, List<Double> improveProbabilities, List<Double> impairProbabilities) {
        Preconditions.checkNotNull(improveProbabilities);
        Preconditions.checkNotNull(impairProbabilities);
        Preconditions.checkState(improveProbabilities.size() == impairProbabilities.size());

        this.variantName = variantName;
        this.improveProbabilities = List.copyOf(improveProbabilities);
        this.impairProbabilities = List.copyOf(impairProbabilities);
    }

    public Optional<String> getVariantName() {
        return Optional.ofNullable(variantName);
    }

    public List<Double> getImproveProbabilities() {
        return improveProbabilities;
    }

    public List<Double> getImpairProbabilities() {
        return impairProbabilities;
    }
}