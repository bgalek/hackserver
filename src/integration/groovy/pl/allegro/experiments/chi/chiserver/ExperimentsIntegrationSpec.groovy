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
}
