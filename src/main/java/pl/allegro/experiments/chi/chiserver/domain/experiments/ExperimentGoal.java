package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;

import java.math.BigDecimal;

import static pl.allegro.experiments.chi.chiserver.util.BigDecimals.round2;

public class ExperimentGoal {
    private final Hypothesis hypothesis;
    private final TestConfiguration testConfiguration;

    public ExperimentGoal(Hypothesis hypothesis, TestConfiguration testConfiguration) {
        Preconditions.checkArgument(hypothesis != null, "experiment goal is invalid without hypothesis");
        this.hypothesis = hypothesis;
        this.testConfiguration = testConfiguration;
    }

    @DiffInclude
    public Hypothesis getHypothesis() {
        return hypothesis;
    }

    public TestConfiguration getTestConfiguration() {
        return testConfiguration;
    }

    public static class Hypothesis {
        private final String leadingMetric;
        private final BigDecimal expectedDiffPercent;

        public Hypothesis(String leadingMetric, BigDecimal expectedDiffPercent) {
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
        private Long requiredSampleSize;

        public TestConfiguration(BigDecimal leadingMetricBaselineValue, BigDecimal testAlpha, BigDecimal testPower, Long requiredSampleSize) {
            this.leadingMetricBaselineValue = round2(leadingMetricBaselineValue);
            this.testAlpha = round2(testAlpha);
            this.testPower = round2(testPower);
            this.requiredSampleSize = requiredSampleSize;
        }

        public BigDecimal getLeadingMetricBaselineValue() {
            return leadingMetricBaselineValue;
        }

        public Long getRequiredSampleSize() {
            return requiredSampleSize;
        }

        public BigDecimal getTestAlpha() {
            return testAlpha;
        }

        public BigDecimal getTestPower() {
            return testPower;
        }
    }
}
