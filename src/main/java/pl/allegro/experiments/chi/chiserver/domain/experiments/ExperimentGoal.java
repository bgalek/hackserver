package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.javers.core.metamodel.annotation.DiffInclude;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Optional;

import static pl.allegro.experiments.chi.chiserver.util.BigDecimals.round2;

public class ExperimentGoal {
    private final Hypothesis hypothesis;
    @Nullable
    private final TestConfiguration testConfiguration;

    public ExperimentGoal(Hypothesis hypothesis,
                          @Nullable TestConfiguration testConfiguration) {
        Preconditions.checkArgument(hypothesis != null, "experiment goal is invalid without hypothesis");
        this.hypothesis = hypothesis;
        this.testConfiguration = testConfiguration;
    }

    @DiffInclude
    public Hypothesis getHypothesis() {
        return hypothesis;
    }

    public Optional<TestConfiguration> getTestConfiguration() {
        return Optional.ofNullable(testConfiguration);
    }

    public boolean hasTestConfiguration() {
        return getTestConfiguration().isPresent();
    }

    public ExperimentGoal updateBaselineMetricValue(double baselineMetricValue) {
        return new ExperimentGoal(hypothesis, testConfiguration.updateBaselineMetricValue(baselineMetricValue));
    }

    public ExperimentGoal updateRequiredSampleSize(Integer requiredSampleSize, Integer currentSampleSize) {
        return new ExperimentGoal(hypothesis, testConfiguration.updateRequiredSampleSize(requiredSampleSize, currentSampleSize));
    }

    public static class Hypothesis {
        private final String leadingMetric;
        private final BigDecimal expectedDiffPercent;

        public Hypothesis(String leadingMetric, BigDecimal expectedDiffPercent) {
            Preconditions.checkArgument(StringUtils.isNotBlank(leadingMetric));

            this.leadingMetric = leadingMetric;
            this.expectedDiffPercent = round2(expectedDiffPercent);
        }

        public String getLeadingMetric() {
            return leadingMetric;
        }

        public BigDecimal getExpectedDiffPercent() {
            return expectedDiffPercent;
        }
    }

    public static class TestConfiguration {
        private final BigDecimal leadingMetricBaselineValue;
        private final BigDecimal testAlpha;
        private final BigDecimal testPower;
        private Integer requiredSampleSize;
        private Integer currentSampleSize;

        public TestConfiguration(BigDecimal leadingMetricBaselineValue,
                                 BigDecimal testAlpha,
                                 BigDecimal testPower,
                                 Integer requiredSampleSize,
                                 Integer currentSampleSize) {
            this.leadingMetricBaselineValue = round2(leadingMetricBaselineValue);
            this.testAlpha = round2(testAlpha);
            this.testPower = round2(testPower);
            this.requiredSampleSize = requiredSampleSize == null ? 0 : requiredSampleSize;
            this.currentSampleSize = currentSampleSize == null ? 0 : currentSampleSize;
        }

        public BigDecimal getLeadingMetricBaselineValue() {
            return leadingMetricBaselineValue;
        }

        public Integer getRequiredSampleSize() {
            return requiredSampleSize;
        }

        public BigDecimal getTestAlpha() {
            return testAlpha;
        }

        public BigDecimal getTestPower() {
            return testPower;
        }

        public Integer getCurrentSampleSize() {
            return currentSampleSize;
        }

        TestConfiguration updateBaselineMetricValue(double baselineMetricValue) {
            return new TestConfiguration(round2(baselineMetricValue), testAlpha, testPower, requiredSampleSize, currentSampleSize);
        }

        TestConfiguration updateRequiredSampleSize(Integer newRequiredSampleSize, Integer newCurrentSampleSize) {
            return new TestConfiguration(leadingMetricBaselineValue, testAlpha, testPower, newRequiredSampleSize, newCurrentSampleSize);
        }
    }
}
