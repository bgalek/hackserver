package pl.allegro.experiments.chi.chiserver.domain.calculator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class SampleSizeCalculator {

    private static final Map<BigDecimal, Double> alphaZScores = ImmutableMap.of(
        round2(0.05), 1.96
    );

    private static final Map<BigDecimal, Double> powerZScores = ImmutableMap.of(
        round2(0.80), 0.84,
        round2(0.85), 1.04,
        round2(0.90), 1.282,
        round2(0.95), 1.645
    );

    public long calculateSampleSize(SampleSizeCalculatorRequest request) {
        Preconditions.checkArgument(request != null);
        Preconditions.checkArgument(isValidPercent(request.getBaselineMetricValue()), "baselineMetricValue should be valid in range (0,100), got:" + request.getBaselineMetricValue());
        Preconditions.checkArgument(isValidPercent(request.getExpectedDiffPercent()), "expectedDiffPercent should be valid in range (0,100), got:" + request.getExpectedDiffPercent());

        Double alphaZScore = alphaZScores.get(round2(request.getTestAlpha()));
        Double powerZScore = powerZScores.get(round2(request.getTestPower()));

        Preconditions.checkArgument(alphaZScore != null, "missing z-score for test alpha " + round2(request.getTestAlpha()));
        Preconditions.checkArgument(powerZScore != null, "missing z-score for test power " + round2(request.getTestPower()));

        if (request.getTestType() != TestType.CHI_SQUARED) {
            throw new IllegalArgumentException("There is no sample size calc " + request.getTestType() + " test");
        }

        var cr = 0.01 * request.getBaselineMetricValue();
        var diff = 0.01 * request.getExpectedDiffPercent();
        var z = 2 * Math.pow(alphaZScore.doubleValue() + powerZScore.doubleValue(), 2);

        return Math.round(z * (cr*(1-cr)) / Math.pow(cr*diff,2));
    }

    public enum TestType {
        CHI_SQUARED
    }

    private boolean isValidPercent(double val) {
        return !(val <= 0 || val >= 100);
    }

    static BigDecimal round2(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN);
    }
}
