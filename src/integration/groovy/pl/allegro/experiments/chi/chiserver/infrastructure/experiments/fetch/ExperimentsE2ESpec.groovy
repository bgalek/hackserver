package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

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
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExampleClientExperiments
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExampleExperiments
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository
import spock.lang.Shared

@DirtiesContext
class ExperimentsE2ESpec extends BaseIntegrationSpec implements ExampleExperiments {

    @ClassRule
    @Shared
    public WireMockRule wireMock = new WireMockRule(port)

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentsRepository instanceof ExperimentsDoubleRepository) {
            throw new RuntimeException("We should test real repository, not the fake one")
        }
        WireMockUtils.teachWireMockJson("/experiments", 'some-experiments.json')
        WireMockUtils.teachWireMockJson("/invalid-experiments", 'invalid-experiments.json')
    }

    def "should return single of experiment loaded from the backing HTTP resource"() {
        given:
        userProvider.user = new User('Anonymous', [], true)
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments/cmuid_regexp'), Map)

        then:
        response.statusCode.value() == 200
        response.body == [
                id              : 'cmuid_regexp',
                renderedVariants: [[name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']]]],
                variantNames    : ['v1'],
                reportingEnabled: true,
                description     : "Experiment description",
                author          : "Experiment owner",
                measurements    : [lastDayVisits: 0],
                groups          : [],
                status          : 'DRAFT',
                editable        : false,
                origin          : 'STASH',
                reportingType   : 'BACKEND',
                eventDefinitions: []
        ]
    }

    def "should return list of assignable experiments in API v.2 for Cient"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200

        and:
        response.body.contains(ExampleClientExperiments.internalExperiment())
        response.body.contains(ExampleClientExperiments.plannedExperiment())
        response.body.contains(ExampleClientExperiments.cmuidRegexpExperiment())
        response.body.contains(ExampleClientExperiments.hashVariantExperiment())
        response.body.contains(ExampleClientExperiments.sampleExperiment())
        response.body.contains(ExampleClientExperiments.timeboundExperiment())
        !response.body.contains(ExampleClientExperiments.experimentFromThePast())
    }

    def "should return all experiments as measured for admin"() {
        given:
        userProvider.user = new User('Anonymous', [], true)
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments'), List)

        then:
        response.statusCode.value() == 200

        and:
        response.body.contains(internalExperiment())
        response.body.contains(plannedExperiment())
        response.body.contains(cmuidRegexpExperiment())
        response.body.contains(cmuidRegexpWithPhoneExperiment())
        response.body.contains(hashVariantExperiment())
        response.body.contains(sampleExperiment())
        response.body.contains(timeboundExperiment())
        response.body.contains(experimentFromThePast())
        response.body.contains(pausedExperiment())
    }

    def "should return last valid list when file is corrupted"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/invalid-experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/experiments'), List)

        then:
        response.statusCode.value() == 200

        and:
        response.body.contains(ExampleClientExperiments.internalExperiment())
        response.body.contains(ExampleClientExperiments.plannedExperiment())
        response.body.contains(ExampleClientExperiments.cmuidRegexpExperiment())
        response.body.contains(ExampleClientExperiments.hashVariantExperiment())
        response.body.contains(ExampleClientExperiments.sampleExperiment())
        response.body.contains(ExampleClientExperiments.timeboundExperiment())
        response.body.contains(ExampleClientExperiments.cmuidRegexpWithPhoneExperiment())
    }

    def "should return BAD_REQUEST when predicate type is incorrect"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : "some2",
                description     : "desc",
                variants        : [
                        [
                                name      : "v1",
                                predicates: [[type: "NOT_SUPPORTED_TYPE"]]
                        ]
                ],
                groups          : [],
                reportingEnabled: true
        ]

        when:
        HttpEntity<Map> entity = new HttpEntity<Map>(request)
        restTemplate.exchange(localUrl('/api/admin/experiments'), HttpMethod.POST, entity, String)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode.value() == 400
    }

    def "should return BAD_REQUEST when no percentage"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id                 : UUID.randomUUID().toString(),
                description        : "desc",
                variantNames       : [],
                internalVariantName: 'v1',
                percentage         : null,
                deviceClass        : null,
                groups             : ['group a', 'group b'],
                reportingEnabled   : true
        ]

        when:
        HttpEntity<Map> entity = new HttpEntity<Map>(request)
        restTemplate.exchange(localUrl('/api/admin/experiments'), HttpMethod.POST, entity, String)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode.value() == 400
    }
}
