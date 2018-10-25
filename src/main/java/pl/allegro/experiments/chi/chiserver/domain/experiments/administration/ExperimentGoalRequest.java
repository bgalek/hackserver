package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

public class ExperimentGoalRequest {
    private String leadingMetric;
    private double expectedDiffPercent;
    private double leadingMetricBaselineValue;
    private double testAlpha;
    private double testPower;
    private long requiredSampleSize;

    private ExperimentGoalRequest() {
    }

    public String getLeadingMetric() {
        return leadingMetric;
    }

    public double getExpectedDiffPercent() {
        return expectedDiffPercent;
    }

    public double getLeadingMetricBaselineValue() {
        return leadingMetricBaselineValue;
    }

    public double getTestAlpha() {
        return testAlpha;
    }

    public double getTestPower() {
        return testPower;
    }

    public long getRequiredSampleSize() {
        return requiredSampleSize;
    }
}
