package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.InternalPredicate
import pl.allegro.experiments.chi.chiserver.domain.experiments.PercentageRange
import spock.lang.Ignore
import spock.lang.Shared

import static com.github.tomakehurst.wiremock.client.WireMock.*

@DirtiesContext
class ExperimentsE2ESpec extends BaseIntegrationSpec {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    def setup() {
        teachWireMockJson("/experiments", '/some-experiments.json')
        teachWireMockJson("/invalid-experiments",'/invalid-experiments.json')
    }

    def "should return list of experiments loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        experimentsRepository.refresh()

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
        experimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments/cmuid_regexp'), Map)

        then:
        response.statusCode.value() == 200
        response.body == [
                id               : 'cmuid_regexp',
                variants         : [[name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']]]],
                reportingEnabled : true,
                description      : "Experiment description",
                author           : "Experiment owner",
                measurements     : [lastDayVisits: 0],
                groups           : [],
                status           : 'DRAFT'
        ]
    }

    def "should return list of active experiments in version 2"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        experimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments/v2'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 6

        and:
        response.body.contains(internalExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
        !response.body.contains(experimentFromThePast())
    }

    def "should return all experiments as measured for admin"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        experimentsRepository.refresh()

        def measuredExperiment = { ex -> ex << [measurements: [lastDayVisits: 0]] }

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 7

        and:
        response.body.contains(measuredExperiment(internalExperiment()))
        response.body.contains(measuredExperiment(cmuidRegexpExperiment()))
        response.body.contains(measuredExperiment(hashVariantExperiment()))
        response.body.contains(measuredExperiment(sampleExperiment()))
        response.body.contains(measuredExperiment(timeboundExperiment()))
        response.body.contains(measuredExperiment(experimentFromThePast()))
    }

    def "should return last valid list when file is corrupted"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        experimentsRepository.refresh()
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/invalid-experiments')
        experimentsRepository.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 6
    }

    def "should create simple experiment"() {
        given:
        def request = [
                id: "some",
                description: "desc",
                variants: [
                    [
                        name: "v1",
                        predicates: [ [ type: "INTERNAL" ]]
                    ]
                ],
                groups: ['group a', 'group b'],
                reportingEnabled: true
        ]

        def expectedExperiment = request + [
                author: "Anonymous",
                status: "DRAFT",
                measurements: [ lastDayVisits: 0 ]
        ]

        when:
        def response = restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List)
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        responseList.body.contains(expectedExperiment)
        responseSingle.body == expectedExperiment
    }

    def "should create experiment with all types of predicates"() {
        given:
        def request = [
                id: "some",
                description: "desc",
                variants: [
                        [
                                name: "v1",
                                predicates: [ [ type: "INTERNAL" ]]
                        ],
                        [
                                name: "v2",
                                predicates: [ [ type: "HASH", from: 17, to: 90 ]]
                        ],
                        [
                                name: "v3",
                                predicates: [ [ type: "CMUID_REGEXP", regexp: "....[123]\$"]]
                        ]
                ],
                groups: ['group a', 'group b'],
                reportingEnabled: true
        ]

        def expectedExperiment = request + [
                author: "Anonymous",
                status: "DRAFT",
                measurements: [ lastDayVisits: 0 ]
        ]

        when:
        def response = restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List)
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        responseList.body.contains(expectedExperiment)
        responseSingle.body == expectedExperiment
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
          reportingEnabled: false,
          groups: [],
          status: 'DRAFT'
        ]
    }

    Map cmuidRegexpExperiment() {
        [ id: 'cmuid_regexp',
          variants: [
                  [ name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']] ]
          ],
          reportingEnabled: true,
          description: "Experiment description",
          author: "Experiment owner",
          groups: [],
          status: 'DRAFT'
        ]
    }

    Map hashVariantExperiment() {
        [ id: 'test_dev',
          variants: [
                  [ name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]] ],
                  [ name: 'v2', predicates: [[type: 'HASH', from: 50, to: 100]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'DRAFT'
        ]
    }

    Map sampleExperiment() {
        [id: 'another_one',
         variants: [
                 [ name: 'v1', predicates: [[type: 'HASH', from: 0, to: 50]] ]
         ],
         reportingEnabled: true,
         description: "Another one",
         author: "Someone",
         groups: [],
         status: 'DRAFT'
        ]
    }

    Map timeboundExperiment() {
        [ id: 'timed_internal_exp',
          activityPeriod: [
              activeFrom: '2017-11-03T10:15:30+02:00',
              activeTo: '2018-11-03T10:15:30+02:00'
          ],
          variants: [
              [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'ACTIVE'
        ]
    }

    Map experimentFromThePast() {
        [ id: 'experiment_from_the_past',
          activityPeriod: [
              activeFrom: '2017-10-01T10:15:30+02:00',
              activeTo: '2017-11-01T10:15:30+02:00'
          ],
          variants: [
                  [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'ENDED'
        ]
    }
}
