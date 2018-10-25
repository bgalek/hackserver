package pl.allegro.experiments.chi.chiserver.domain.calculator;

import static pl.allegro.experiments.chi.chiserver.domain.calculator.SampleSizeCalculator.TestType.CHI_SQUARED;

public class SampleSizeCalculatorRequest {
    private final SampleSizeCalculator.TestType testType = CHI_SQUARED;

    private final double testAlpha;
    private final double testPower;
    private final double baselineMetricValue;
    private final double expectedDiffPercent;

    public SampleSizeCalculatorRequest(
            double testAlpha,
            double testPower,
            double baselineMetricValue,
            double expectedDiffPercent) {
        this.testAlpha = testAlpha;
        this.testPower = testPower;
        this.baselineMetricValue = baselineMetricValue;
        this.expectedDiffPercent = expectedDiffPercent;
    }

    public SampleSizeCalculator.TestType getTestType() {
        return testType;
    }

    public double getTestAlpha() {
        return testAlpha;
    }

    public double getTestPower() {
        return testPower;
    }

    public double getBaselineMetricValue() {
        return baselineMetricValue;
    }

    public double getExpectedDiffPercent() {
        return expectedDiffPercent;
    }

    @Override
    public String toString() {
        return "SampleSizeCalculatorRequest{" +
                "testType=" + testType +
                ", testAlpha=" + testAlpha +
                ", testPower=" + testPower +
                ", baselineMetricValue=" + baselineMetricValue +
                ", expectedDiffPercent=" + expectedDiffPercent +
                '}';
    }
}
