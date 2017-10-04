package pl.allegro.experiments.chi.chiserver

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.persistence.FileBasedExperimentsRepository
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse
import static com.github.tomakehurst.wiremock.client.WireMock.get
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo

class ExperimentsIntegrationSpec extends BaseIntegrationSpec {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(0)

    @Autowired
    RestTemplate restTemplate

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    def setup() {
        teachWireMockJson("/experiments",  this.getClass().getResource('/some-experiments.json' ).text)
        teachWireMockJson("/invalid-experiments",  this.getClass().getResource('/invalid-experiments.json' ).text)
    }

    def "should return list of experiments loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.changeJsonUrl("http://localhost:${wireMock.port()}/experiments")
        fileBasedExperimentsRepository.secureRefresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 5

        and:
        response.body.contains(internalExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
    }

    def "should return list of experiment in version 1"() {
        given:
            fileBasedExperimentsRepository.changeJsonUrl("http://localhost:${wireMock.port()}/experiments/v1")
            fileBasedExperimentsRepository.secureRefresh()

        when:
            def response = restTemplate.getForEntity(localUrl('/api/experiments/v1'), List)

        then:
            response.statusCode.value() == 200
            response.body.size() == 5

        and:
            response.body.contains(internalExperiment())
            response.body.contains(cmuidRegexpExperiment())
            response.body.contains(hashVariantExperiment())
            response.body.contains(sampleExperiment())
            response.body.contains(timeboundExperiment())
    }

    def "should return last valid list when file is corrupted"() {
        given:
        fileBasedExperimentsRepository.changeJsonUrl("http://localhost:${wireMock.port()}/experiments")
        fileBasedExperimentsRepository.secureRefresh()

        fileBasedExperimentsRepository.secureRefresh()
        fileBasedExperimentsRepository.changeJsonUrl("http://localhost:${wireMock.port()}//invalid-experiments")

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 5
    }

    void teachWireMockJson(String path, String json) {
        stubFor(get(urlEqualTo(path))
                .willReturn(aResponse()
                .withStatus(200)
                .withBody(json)))
    }

    Map internalExperiment() {
        return [ id: 'internal_exp',
                 variants: [
                         [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
                 ]
        ]
    }

    Map cmuidRegexpExperiment() {
        return [ id: 'cmuid_regexp',
                 variants: [
                         [ name: 'v1', predicates: [[ type: 'CMUID_REGEXP', regexp: '.*[0-3]$' ]] ]
                 ]
        ]
    }

    Map hashVariantExperiment() {
        return [ id: 'test_dev',
                 variants: [
                         [ name: 'v1', predicates: [[ type: 'HASH', from: 0, to: 50 ]] ],
                         [ name: 'v2', predicates: [[ type: 'HASH', from: 50, to: 100 ]] ]
                 ]
        ]
    }

    Map sampleExperiment() {
        return [ id: 'another_one',
                 variants: [
                         [ name: 'v1', predicates: [[ type: 'HASH', from: 0, to: 50 ]] ]
                 ]
        ]
    }

    Map timeboundExperiment() {
        return [ id:'timed_internal_exp',
                 activeFrom: '2017-11-03T10:15:30+02:00',
                 activeTo: '2017-12-03T10:15:30+02:00',
                 variants: [
                         [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
                 ]
        ]
    }
}
