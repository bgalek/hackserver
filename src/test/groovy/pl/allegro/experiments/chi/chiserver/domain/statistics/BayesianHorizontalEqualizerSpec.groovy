package pl.allegro.experiments.chi.chiserver.domain.statistics

import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianChartMetadata
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianHorizontalEqualizer
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianVerticalEqualizer
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.EqualizerBar
import spock.lang.Specification

class BayesianHorizontalEqualizerSpec extends Specification {

    def "should build Horizontal Equalizer from Vertical Equalizer"(){
        when:
        def vEqualizer = new BayesianVerticalEqualizer(new BayesianChartMetadata("ex1", DeviceClass.phone, 0.1, "2013-12-12"), [
                new EqualizerBar("variant-a",[0.2, 0.0, 0.8], [0.1, 0.5, 0.0]),
                new EqualizerBar("variant-a",[0.3, 0.2, 0.4], [0.0, 0.4, 0.1]),
        ])
        def equalizer = new BayesianHorizontalEqualizer(vEqualizer)

        then:
        equalizer.metadata.boxSize == 0.1
        equalizer.boxRadius == 3
        equalizer.joinedBar.improvingProbabilities == [0.3, 0.2, 0.8]
        equalizer.joinedBar.worseningProbabilities == [0.1, 0.5, 0.1]
    }
}
