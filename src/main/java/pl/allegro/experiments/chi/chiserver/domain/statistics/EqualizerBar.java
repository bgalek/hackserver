package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;

import java.util.List;
import java.util.Optional;

public class EqualizerBar {
    private final String variantName; //nullable
    private final List<Double> improvingProbabilities;
    private final List<Double> worseningProbabilities;

    public EqualizerBar(String variantName, List<Double> improvingProbabilities, List<Double> worseningProbabilities) {
        Preconditions.checkNotNull(improvingProbabilities);
        Preconditions.checkNotNull(worseningProbabilities);
        Preconditions.checkState(improvingProbabilities.size() == worseningProbabilities.size());

        this.variantName = variantName;
        this.improvingProbabilities = List.copyOf(improvingProbabilities);
        this.worseningProbabilities = List.copyOf(worseningProbabilities);
    }

    public Optional<String> getVariantName() {
        return Optional.ofNullable(variantName);
    }

    public List<Double> getImprovingProbabilities() {
        return improvingProbabilities;
    }

    public List<Double> getWorseningProbabilities() {
        return worseningProbabilities;
    }
}