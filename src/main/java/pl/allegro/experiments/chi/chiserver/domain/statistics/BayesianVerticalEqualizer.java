package pl.allegro.experiments.chi.chiserver.domain.statistics;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics.Sample;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLOutput;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BayesianVerticalEqualizer extends BayesianEqualizer{
    private final List<EqualizerBar> bars;

    private static final double BOX_SIZE = 0.01;
    private static final int MAX_BOX = 5;

    public BayesianVerticalEqualizer(BayesianExperimentStatistics experimentStatistics) {

        this(experimentStatistics.getExperimentId(),
                DeviceClass.fromString(experimentStatistics.getDevice()),
                BOX_SIZE,
                experimentStatistics.getVariantBayesianStatistics().stream()
                        .filter(it -> !it.getVariantName().equals("base")) //?? why base here
                        .map(BayesianVerticalEqualizer::toBar)
                        .collect(Collectors.toList()));
    }

    private static EqualizerBar toBar(VariantBayesianStatistics statistics) {
        int samplesCount = statistics.allCount();
        List<Sample> samples = statistics.getSamplesAsList();

        List<BigDecimal> improvingProbabilities =
                IntStream.range(0, MAX_BOX).mapToObj(box -> {
                    double thres = BOX_SIZE * box;

                    int samplesInBox = samples.stream()
                            .filter(s -> s.getValue() >= thres)
                            .mapToInt(s -> s.getCount()).sum();

                    return toRatio(samplesInBox, samplesCount);
                }).collect(Collectors.toList());

        List<BigDecimal> worseningProbabilities =
            IntStream.range(0, MAX_BOX).mapToObj(box -> {
            double thres = -1 * BOX_SIZE * box;

            int samplesInBox = samples.stream()
                    .filter(s -> s.getValue() <= thres)
                    .mapToInt(s -> s.getCount()).sum();

            return toRatio(samplesInBox, samplesCount);
        }).collect(Collectors.toList());

        return new EqualizerBar(statistics.getVariantName(), improvingProbabilities, worseningProbabilities);
    }

    private static BigDecimal toRatio(int sampleCount, int allCount) {
        return BigDecimal.valueOf(sampleCount / (double) allCount).setScale(4, RoundingMode.HALF_DOWN);
    }

    BayesianVerticalEqualizer(String experimentId, DeviceClass deviceClass, double boxSize, List<EqualizerBar> bars) {
        super(experimentId, deviceClass, boxSize);
        Preconditions.checkNotNull(bars);
        this.bars = List.copyOf(bars);
    }

    public List<EqualizerBar> getBars() {
        return bars;
    }
}
