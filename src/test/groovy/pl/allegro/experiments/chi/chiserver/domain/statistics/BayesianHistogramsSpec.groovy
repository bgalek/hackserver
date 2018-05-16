package pl.allegro.experiments.chi.chiserver.domain.statistics

import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianHistograms
import spock.lang.Specification

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

        histogram.values == givenStatsVariantA.samples.values
        histogram.counts == givenStatsVariantA.samples.counts

        histogram.labels[0] == "0.01%"
        histogram.labels[89] == "7.35%"
        histogram.labels[99] == "20.31%"
        histogram.labels[100] == "79.69%"
        histogram.labels[109] == "61.05%"
        histogram.labels[199] == "0.12%"
    }
}
