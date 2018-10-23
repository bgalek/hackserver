package pl.allegro.experiments.chi.chiserver.domain.calculator

import spock.lang.Specification

class SampleSizeCalculatorTest extends Specification {

    def "should calculate sample size for Chi2 test"(){
        given:
        def req = new SampleSizeCalculatorRequest(0.05, 0.85, 5, 2)

        when:
        def result = new SampleSizeCalculator().calculateSampleSize(req)

        then:
        result == 855000
    }

    def "should fail to calculate when no z-score entries are defined inside calculator"() {
        when:
        new SampleSizeCalculator().calculateSampleSize(new SampleSizeCalculatorRequest(0.5, 0.85, 5, 2))

        then:
        thrown(IllegalArgumentException)
    }

    def "should fail to calculate when baseline conversion is 0"(){
        when:
        new SampleSizeCalculator().calculateSampleSize(new SampleSizeCalculatorRequest(0.05, 0.85, 5, 0))

        then:
        thrown(IllegalArgumentException)
    }

    def "should fail to calculate when expected diff is 0"(){
        when:
        new SampleSizeCalculator().calculateSampleSize(new SampleSizeCalculatorRequest(0.05, 0.85, 0, 2))

        then:
        thrown(IllegalArgumentException)
    }
}
