package pl.allegro.experiments.chi.chiserver

import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import spock.lang.Specification


class JsonConverterIntegrationSpec extends Specification {
    def "should deserialize json with experiments"() {
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

        then:
        println result;
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
