package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;

public class EqualizerBar {
    private final String variantName; //nullable
    private final List<Double> improveProbabilities;
    private final List<Double> worsenProbabilities;

    public EqualizerBar(String variantName, List<Double> improveProbabilities, List<Double> worsenProbabilities) {
        Preconditions.checkNotNull(improveProbabilities);
        Preconditions.checkNotNull(worsenProbabilities);
        Preconditions.checkState(improveProbabilities.size() == worsenProbabilities.size());

        this.variantName = variantName;
        this.improveProbabilities = List.copyOf(improveProbabilities);
        this.worsenProbabilities = List.copyOf(worsenProbabilities);
    }

    public Optional<String> getVariantName() {
        return Optional.ofNullable(variantName);
    }

    public List<Double> getImproveProbabilities() {
        return improveProbabilities;
    }

    public List<Double> getWorsenProbabilities() {
        return worsenProbabilities;
    }
}