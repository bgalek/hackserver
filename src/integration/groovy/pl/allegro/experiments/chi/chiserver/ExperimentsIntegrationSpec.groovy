package pl.allegro.experiments.chi.chiserver

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.infrastructure.FileBasedExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepositoryRefresher

class ExperimentsIntegrationSpec extends WireMockedIntegrationSpec {

    @Autowired
    RestTemplate restTemplate

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    InMemoryExperimentsRepository inMemoryExperimentsRepository

    @Autowired
    InMemoryExperimentsRepositoryRefresher refresher

    def setup() {
        fileBasedExperimentsRepository.setFileUrl("http://localhost:${wireMockPort}/some-experiments")
    }

    def "should return list of experiments"() {
        given:
        teachWireMockJson("/some-experiments",  this.getClass().getResource('/some-experiments.json' ).text)
        refresher.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 5
    }

    def "should return empty list when file is corrupted"() {
        given:
        inMemoryExperimentsRepository.updateExperiments([])
        teachWireMockJson("/some-experiments",  this.getClass().getResource('/invalid-experiments.json' ).text)
        refresher.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 0
    }

    def "should return last valid list when file is corrupted"() {
        given:
        inMemoryExperimentsRepository.updateExperiments([])
        teachWireMockJson("/some-experiments",  this.getClass().getResource('/some-experiments.json' ).text)
        refresher.refresh()
        teachWireMockJson("/some-experiments",  this.getClass().getResource('/invalid-experiments.json' ).text)
        refresher.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 5
    }
}
