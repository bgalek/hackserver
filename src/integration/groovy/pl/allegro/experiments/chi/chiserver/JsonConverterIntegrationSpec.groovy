package pl.allegro.experiments.chi.chiserver

import pl.allegro.experiments.chi.chiserver.domain.InternalExperimentVariant
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime


class JsonConverterIntegrationSpec extends Specification {

    def "should deserialize json with experiment"() {
        given:
        def converter = new JsonConverter()
        def input = '''
            [{
               "id": "experiment",
                "activeFrom": "2017-11-03T10:15:30+02:00",
                "activeTo": "2018-11-03T10:15:30+02:00",
                "variants": [{
                    "type":"INTERNAL",
                    "name": "internal"
                }]
            }]
        '''

        when:
        def result = converter.fromJSON(input)
        def experiment = result.get(0)

        then:
        result.size() == 1
        experiment.id == 'experiment'
        experiment.variants*.name == ['internal']
        experiment.variants*.type == ['INTERNAL']
        experiment.activeFrom == ZonedDateTime.of(2017, 11, 03, 10, 15, 30, 0, ZoneId.of('+02:00'))
        experiment.activeTo == ZonedDateTime.of(2018, 11, 03, 10, 15, 30, 0, ZoneId.of('+02:00'))
    }

    def "should deserialize experiments from file"() {
        given:
        String input = this.getClass().getResource('/some-experiments.json' ).text
        def converter = new JsonConverter()

        when:
        def result = converter.fromJSON(input)

        then:
        result.size() == 5
    }
}
