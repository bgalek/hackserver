package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

import java.time.ZonedDateTime

@DirtiesContext
class ExperimentsSelfServiceE2ESpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    UserProvider userProvider

    def setup() {
        if (!experimentsRepository instanceof ExperimentsDoubleRepository) {
            throw new RuntimeException("We should test real repository, not the fake one")
        }
    }

    def "should set editable flag depending on who ask for single experiment/multiple experiments"() {
        given:
        userProvider.user = new User('Author', [], true)
        def request = [
                id              : UUID.randomUUID().toString(),
                description     : "desc",
                variants        : [
                        [
                                name      : "v1",
                                predicates: [[type: "INTERNAL"]]
                        ]
                ],
                groups          : ['group a', 'group b'],
                reportingEnabled: true
        ]
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)
        userProvider.user = user

        when:
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        responseSingle.body.editable == editable

        when:
        def responseMultiple = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List)

        then:
        responseMultiple.body.find {experiment -> experiment.id == request.id}.editable == editable

        where:
        user                                    | editable
        new User('Author', [], true)            | true
        new User('Author', [], false)           | true
        new User('Author', ['group a'], false)  | true
        new User('Author', ['group a'], true)   | true
        new User('Author', [], false)           | true
        new User('Unknown', [], false)          | false
        new User('Unknown', ['group a'], false) | true
        new User('Unknown', [], true)           | true
        new User('Unknown', ['group a'], true)  | true
    }

    def "should execute all administration commands"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        def request = [
                id              : "some2",
                description     : "desc",
                documentLink     : "https://vuetifyjs.com/vuetify/quick-start",
                variants        : [
                        [
                                name      : "v1",
                                predicates: [[type: "INTERNAL"]]
                        ],
                        [
                                name      : "v2",
                                predicates: [[type: "HASH", from: 17, to: 90]]
                        ],
                        [
                                name      : "v3",
                                predicates: [[type: "CMUID_REGEXP", regexp: "....[123]\$"], [type: "DEVICE_CLASS", device: "phone"]]
                        ]
                ],
                groups          : ['group a', 'group b'],
                reportingEnabled: true
        ]
        
        when:
        def response = restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List)
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        def expectedExperiment = request + [
                author      : "Anonymous",
                status      : "DRAFT",
                measurements: [lastDayVisits: 0],
                editable: true,
                origin: 'mongo'
        ]
        responseSingle.body.variants == expectedExperiment.variants
        responseSingle.body == expectedExperiment
        responseList.body.contains(expectedExperiment)

        and:
        def startRequest = [
                experimentDurationDays: 30
        ]

        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/start"), startRequest, Map)
        def startedExperiment = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        startedExperiment.body.status == 'ACTIVE'

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/prolong"), [experimentAdditionalDays: 30], Map)
        def prolongedExperiment = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        def expectedActiveTo = ZonedDateTime.parse(startedExperiment.body.activityPeriod.activeTo).plusDays(30).toString()
        prolongedExperiment.body.activityPeriod.activeTo == expectedActiveTo

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/stop"), Map)
        def stoppedExperiment = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        stoppedExperiment.body.status == 'ENDED'

        and:
        restTemplate.delete(localUrl("/api/admin/experiments/${request.id}"))

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        thrown HttpClientErrorException
    }
}

