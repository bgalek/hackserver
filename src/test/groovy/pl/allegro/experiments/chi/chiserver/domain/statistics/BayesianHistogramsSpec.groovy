package pl.allegro.experiments.chi.chiserver.domain.statistics

import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianHistograms
import spock.lang.Specification

import java.math.RoundingMode

class BayesianHistogramsSpec extends Specification {
    def "should build Histogram from raw Bayesian stats"(){
        given:
        def givenStats = BayesianVerticalEqualizerSpec.sampleStats()
        def givenStatsVariantA = givenStats.variantBayesianStatistics.find{it.variantName == "test-a"}

        when:
        def histograms = new BayesianHistograms(givenStats)

        then:
        histograms.variantHistograms.size() == 2

        def histogram = histograms.variantHistograms.find{it.variantName == "test-a" }

        histogram.values.size() == 100
        histogram.labels.size() == 100
        histogram.frequencies.size() == 100

        histogram.values[0]  == -0.1
        histogram.values[49] == -0.002
        histogram.values[50] ==  0.002
        histogram.values[99] ==  0.1

        histogram.frequencies[50] == toRatio4(givenStatsVariantA.samples.counts[100] +
                                              givenStatsVariantA.samples.counts[101],
                                              givenStatsVariantA.allCount())

        histogram.labels[0]  == 0.0
        histogram.labels[40] == 9.2
        histogram.labels[49] == 20.3
        histogram.labels[50] == 79.7
        histogram.labels[59] == 63.4
        histogram.labels[99] == 2.1
    }

    BigDecimal toRatio4(int sample, int allCount) {
        new BigDecimal(sample/(double)allCount).setScale(4, RoundingMode.HALF_DOWN)
    }
}
