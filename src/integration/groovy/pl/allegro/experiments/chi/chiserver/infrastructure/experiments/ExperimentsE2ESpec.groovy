package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.github.tomakehurst.wiremock.junit.WireMockRule
import org.junit.ClassRule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
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

    @Autowired
    ExperimentRepositoryRefresher refresher

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentsRepository instanceof ExperimentsDoubleRepository) {
            throw new RuntimeException("We should test real repository, not the fake one")
        }
        teachWireMockJson("/experiments", '/some-experiments.json')
        teachWireMockJson("/invalid-experiments",'/invalid-experiments.json')
    }

    def "should return list of experiments loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        refresher.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 7

        and:
        response.body.contains(internalExperiment())
        response.body.contains(plannedExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
    }

    def "should return single of experiment loaded from the backing HTTP resource"() {
        given:
        userProvider.user = new User('Anonymous', [], true)
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        refresher.refresh()

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
                status           : 'DRAFT',
                editable         : true
        ]
    }

    def "should return list of overridable experiments in version 2"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        refresher.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 7

        and:
        response.body.contains(internalExperiment())
        response.body.contains(plannedExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
        !response.body.contains(experimentFromThePast())
    }

    def "should return all experiments as measured for admin"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        refresher.refresh()

        def measuredExperiment = { ex -> ex << [measurements: [lastDayVisits: 0]] }

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 8

        and:
        response.body.contains(measuredExperiment(internalExperiment()))
        response.body.contains(measuredExperiment(plannedExperiment()))
        response.body.contains(measuredExperiment(cmuidRegexpExperiment()))
        response.body.contains(measuredExperiment(hashVariantExperiment()))
        response.body.contains(measuredExperiment(sampleExperiment()))
        response.body.contains(measuredExperiment(timeboundExperiment()))
        response.body.contains(measuredExperiment(experimentFromThePast()))
    }

    def "should return last valid list when file is corrupted"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/experiments')
        refresher.refresh()
        fileBasedExperimentsRepository.jsonUrl = resourceUrl('/invalid-experiments')
        refresher.refresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200
        response.body.size() == 7
    }

    def "should return BAD_REQUEST when predicate type is incorrect"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id: "some2",
                description: "desc",
                variants: [
                        [
                                name: "v1",
                                predicates: [ [ type: "NOT_SUPPORTED_TYPE" ]]
                        ]
                ],
                groups: [],
                reportingEnabled: true
        ]

        when:
        HttpEntity<Map> entity = new HttpEntity<Map>(request)
        restTemplate.exchange(localUrl('/api/admin/experiments'), HttpMethod.POST, entity, String)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode.value() == 400
    }

    def "should return BAD_REQUEST when predicate has no name"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id: "some2",
                description: "desc",
                variants: [
                        [
                                predicates: [ [ type: "INTERNAL" ]]
                        ]
                ],
                groups: [],
                reportingEnabled: true
        ]

        when:
        HttpEntity<Map> entity = new HttpEntity<Map>(request)
        restTemplate.exchange(localUrl('/api/admin/experiments'), HttpMethod.POST, entity, String)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode.value() == 400
    }

    def "should return BAD_REQUEST when predicate has no required properties"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id: "some2",
                description: "desc",
                variants: [
                        [
                                name: "v1",
                                predicates: [ [ type: "HASH" ] ]
                        ]
                ],
                groups: [],
                reportingEnabled: true
        ]

        when:
        HttpEntity<Map> entity = new HttpEntity<Map>(request)
        restTemplate.exchange(localUrl('/api/admin/experiments'), HttpMethod.POST, entity, String)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode.value() == 400
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

    Map plannedExperiment() {
        [ id: 'planned_exp',
          activityPeriod: [
                  "activeFrom": "2050-11-03T10:15:30+02:00",
                  "activeTo": "2060-11-03T10:15:30+02:00"
          ],
          variants: [
              [ name: 'internal', predicates: [[ type:'INTERNAL' ]] ]
          ],
          reportingEnabled: true,
          groups: [],
          status: 'PLANNED'
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
