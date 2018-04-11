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
        WireMockUtils.teachWireMockJson("/experiments", '/some-experiments.json')
        WireMockUtils.teachWireMockJson("/invalid-experiments", '/invalid-experiments.json')
    }

    def "should return list of experiments loaded from the backing HTTP resource"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

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
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments/cmuid_regexp'), Map)

        then:
        response.statusCode.value() == 200
        response.body == [
                id              : 'cmuid_regexp',
                variants        : [[name: 'v1', predicates: [[type: 'CMUID_REGEXP', regexp: '.*[0-3]$']]]],
                reportingEnabled: true,
                description     : "Experiment description",
                author          : "Experiment owner",
                measurements    : [lastDayVisits: 0],
                groups          : [],
                status          : 'DRAFT',
                editable        : true,
                origin          : 'stash',
                reportingType   : 'BACKEND',
                eventDefinitions: []
        ]
    }

    def "should return list of overridable experiments in version 2"() {
        given:
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

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
        userProvider.user = new User('Anonymous', [], true)
        fileBasedExperimentsRepository.jsonUrl = WireMockUtils.resourceUrl('/experiments', wireMock)
        fileBasedExperimentsRepository.secureRefresh()

        def editableMeasuredExperiment = { ex -> ex << [measurements: [lastDayVisits: 0], editable: true, origin: 'stash'] }

        when:
        def response = restTemplate.getForEntity(localUrl('/api/admin/experiments'), List)

        then:
        def expectedExperiments = [
                internalExperiment(),
                plannedExperiment(),
                cmuidRegexpExperiment(),
                cmuidRegexpWithPhoneExperiment(),
                hashVariantExperiment(),
                sampleExperiment(),
                timeboundExperiment(),
                experimentFromThePast(),
                pausedExperiment()
        ].collect { editableMeasuredExperiment(it) }
        response.statusCode.value() == 200
        response.body.size() == expectedExperiments.size()

        and:
        //response.body.findAll{ it["id"] == "cmuid_with_phone"}
        response.body.containsAll(expectedExperiments)
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
        response.body.size() == 7
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

    def "should return BAD_REQUEST when predicate has no name"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : "some2",
                description     : "desc",
                variants        : [
                        [
                                predicates: [[type: "INTERNAL"]]
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

    def "should return BAD_REQUEST when predicate has no required properties"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : "some2",
                description     : "desc",
                variants        : [
                        [
                                name      : "v1",
                                predicates: [[type: "HASH"]]
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
}
