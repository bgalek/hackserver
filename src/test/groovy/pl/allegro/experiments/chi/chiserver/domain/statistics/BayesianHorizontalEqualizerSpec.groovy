package pl.allegro.experiments.chi.chiserver.domain.statistics

import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass
import spock.lang.Specification

class BayesianHorizontalEqualizerSpec extends Specification {

    def "should build Horizontal Equalizer from Vertical Equalizer"(){
        when:
        def vEqualizer = new BayesianVerticalEqualizer("ex1", DeviceClass.phone, 0.1, [
                new EqualizerBar("variant-a",[0.2, 0.0, 0.8], [0.1, 0.5, 0.0]),
                new EqualizerBar("variant-a",[0.3, 0.2, 0.4], [0.0, 0.4, 0.1]),
        ])
        def equalizer = new BayesianHorizontalEqualizer(vEqualizer)

        then:
        equalizer.boxSize == 0.1
        equalizer.boxRadius == 3
        equalizer.joinedBar.improvingProbabilities == [0.3, 0.2, 0.8]
        equalizer.joinedBar.worseningProbabilities == [0.0, 0.4, 0.0]
    }
}
