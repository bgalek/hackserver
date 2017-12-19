package pl.allegro.experiments.chi.chiserver.experiments

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

class ExperimentsIntegrationSpec extends BaseIntegrationSpec {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    def setup() {
        teachWireMockJson("/experiments", '/some-experiments.json')
        teachWireMockJson("/invalid-experiments",'/invalid-experiments.json')
    }

    def "should return list of experiments loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        fileBasedExperimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 6

        and:
        response.body.contains(internalExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
    }

    def "should return single of experiment loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        fileBasedExperimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments/cmuid_regexp'), Map)

        then:
        response.statusCode.value() == 200
        response.body == [
            id: 'cmuid_regexp',
            variants: [ [ name: 'v1', predicates: [ [ type: 'CMUID_REGEXP', regexp: '.*[0-3]$'] ] ] ],
            reportingEnabled: true,
            description: "Experiment description",
            owner: "Experiment owner"
        ]
    }

    def "should return list of experiment in version 1"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        fileBasedExperimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments/v1'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 6

        and:
        response.body.contains(internalExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
    }

    def "should return last valid list when file is corrupted"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        fileBasedExperimentsRepository.refresh()
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/invalid-experiments')
        fileBasedExperimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 6
    }

    void teachWireMockJson(String path, String jsonPath) {
        String json = getResourceAsString(jsonPath)
        stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(json)))
    }

    String getResourceAsString(String resourceName) {
        this.getClass().getResource(resourceName).text
    }

    String resourceUrl(String endpoint) {
        "http://localhost:${wireMock.port()}$endpoint"
    }

    Map internalExperiment() {
        [ id: 'internal_exp',
          variants: [
                  [ name: 'internal', predicates: [[type: 'INTERNAL']] ]
          ],
          reportingEnabled: false
        ]
    }

    Map cmuidRegexpExperiment() {
        [ id: 'cmuid_regexp',
          variants: [
                  [ name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']] ]
          ],
          reportingEnabled: true,
          description: "Experiment description",
          owner: "Experiment owner"
        ]
    }

    Map hashVariantExperiment() {
        [ id: 'test_dev',
          variants: [
                  [ name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]] ],
                  [ name: 'v2', predicates: [[type: 'HASH', from: 50, to: 100]] ]
          ],
          reportingEnabled: true
        ]
    }

    Map sampleExperiment() {
        [id: 'another_one',
         variants: [
                 [ name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]] ]
         ],
         reportingEnabled: true,
         description: "Another one",
         owner: "Someone"
        ]
    }

    Map timeboundExperiment() {
        [ id:'timed_internal_exp',
          activeFrom: '2017-11-03T10:15:30+02:00',
          activeTo: '2018-11-03T10:15:30+02:00',
          variants: [
                  [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true
        ]
    }
}
