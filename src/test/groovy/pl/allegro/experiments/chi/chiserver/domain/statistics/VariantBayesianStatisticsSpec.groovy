package pl.allegro.experiments.chi.chiserver.domain.statistics

import spock.lang.Specification

class VariantBayesianStatisticsSpec extends Specification {
    def "should calculate worsening probabilities "(){
        given:
        def givenStats = BayesianVerticalEqualizerSpec.sampleStats()
        def givenStatsVariantA = givenStats.variantBayesianStatistics.find{it.variantName == "test-a"}

       when:
        def probabilities = givenStatsVariantA.calculateWorseningProbabilities(0.01, 10)

        for (int i=0; i< probabilities.size(); i++) {
            println (i +". box label probability: "+ probabilities.get(i))
        }

        then:
        for (int i=0; i< probabilities.size(); i++) {
            def j = (i+1)*10
            println (j +". mass: "+ givenStatsVariantA.massLeft(j))
            assert probabilities.get(i) == givenStatsVariantA.massLeft(j)
        }

        when:
        probabilities = givenStatsVariantA.calculateWorseningProbabilities(0.002, 50)

        then:
        for (int i=0; i< probabilities.size(); i++) {
            def j = (i+1)*2
            assert probabilities.get(i) == givenStatsVariantA.massLeft(j)
        }
    }

    def "should calculate improving probabilities "(){
        given:
        def givenStats = BayesianVerticalEqualizerSpec.sampleStats()
        def givenStatsVariantA = givenStats.variantBayesianStatistics.find{it.variantName == "test-a"}


        when:
        def probabilities = givenStatsVariantA.calculateImprovingProbabilities(0.01, 10)

        for (int i=0; i< probabilities.size(); i++) {
            println (i +". box label probability: "+ probabilities.get(i))
        }

        then:
        for (int i=0; i< probabilities.size(); i++) {
            def j = 100 + (i * 10)
            println(i + ". from:" + j + ", mass: " + givenStatsVariantA.massRight(j))
            assert probabilities.get(i) == givenStatsVariantA.massRight(j)
        }

        when:
        probabilities = givenStatsVariantA.calculateImprovingProbabilities(0.002, 50)

        then:
        for (int i=0; i< probabilities.size(); i++) {
            def j = 100 + (i * 2)
            assert probabilities.get(i) == givenStatsVariantA.massRight(j)
        }
    }
}
