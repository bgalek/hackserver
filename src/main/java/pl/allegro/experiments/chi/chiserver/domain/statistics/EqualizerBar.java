package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class EqualizerBar {
    private final String variantName; //nullable
    private final List<BigDecimal> improvingProbabilities;
    private final List<BigDecimal> worseningProbabilities;

    public EqualizerBar(String variantName, List<BigDecimal> improvingProbabilities, List<BigDecimal> worseningProbabilities) {
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

    public List<BigDecimal> getImprovingProbabilities() {
        return improvingProbabilities;
    }

    public List<BigDecimal> getWorseningProbabilities() {
        return worseningProbabilities;
    }
}