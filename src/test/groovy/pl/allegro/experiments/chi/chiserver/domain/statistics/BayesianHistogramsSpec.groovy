package pl.allegro.experiments.chi.chiserver.domain.statistics

import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianHistograms
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics
import spock.lang.Specification

import java.math.RoundingMode

class BayesianHistogramsSpec extends Specification {

    def "should build Histogram from raw Bayesian stats"(){
        given:
        def givenStats = BayesianVerticalEqualizerSpec.sampleStats()
        def givenStatsVariantA = givenStats.variantBayesianStatistics.find{it.variantName == "test-a"}

        when:
        def histograms = new BayesianHistograms(givenStats)
        def histogram = histograms.variantHistograms.find{it.variantName == "test-a" }

        then:
        histograms.variantHistograms.size() == 2

        histogram.values.size() == 100
        histogram.labels.size() == 100
        histogram.frequencies.size() == 100

        histogram.values[0]  == -0.1
        histogram.values[49] == -0.002
        histogram.values[50] ==  0.002
        histogram.values[99] ==  0.1

        histogram.frequencies[70] == toRatio6(givenStatsVariantA.samples.counts[140] +
                                              givenStatsVariantA.samples.counts[141],
                                              givenStatsVariantA.allCount())

        histogram.frequencies[50] == toRatio6(givenStatsVariantA.samples.counts[100] +
                                              givenStatsVariantA.samples.counts[101],
                                              givenStatsVariantA.allCount())

        round1( histogram.labels[40] ) == round1(100 * givenStatsVariantA.massLeft(82))
        round1( histogram.labels[50] ) == round1(100 * givenStatsVariantA.massRight(100))
        round1( histogram.labels[60] ) == round1(100 * givenStatsVariantA.massRight(120))
        round1( histogram.labels[70] ) == round1(100 * givenStatsVariantA.massRight(140))
    }

    BigDecimal toRatio6(int sample, int allCount) {
        new BigDecimal(sample/(double)allCount).setScale(6, RoundingMode.HALF_DOWN)
    }

    BigDecimal round1(BigDecimal val) {
        val.setScale(1, RoundingMode.HALF_DOWN)
    }
}
