package pl.allegro.experiments.chi.chiserver.application.administration

import org.javers.core.Javers
import org.javers.core.changelog.SimpleTextChangeLog
import org.javers.repository.jql.QueryBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository

import java.time.ZonedDateTime

@DirtiesContext
class ExperimentsSelfServiceE2ESpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    UserProvider userProvider

    @Autowired
    Javers javers

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
        responseMultiple.body.find { experiment -> experiment.id == request.id }.editable == editable

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
                documentLink    : "https://vuetifyjs.com/vuetify/quick-start",
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
                editable    : true,
                origin      : 'mongo'
        ]
        responseSingle.body.variants == expectedExperiment.variants
        responseSingle.body == expectedExperiment
        responseList.body.contains(expectedExperiment)

        when:
        def startRequest = [
                experimentDurationDays: 30
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/start"), startRequest, Map)

        then:
        assertThatExperimentWithId(request.id)
                .hasStatus(ExperimentStatus.ACTIVE)

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/pause"), Map)

        then:
        assertThatExperimentWithId(request.id)
                .hasStatus(ExperimentStatus.PAUSED)

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/resume"), Map)

        then:
        assertThatExperimentWithId(request.id)
                .hasStatus(ExperimentStatus.ACTIVE)

        when:
        def startedExperiment = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/prolong"), [experimentAdditionalDays: 30], Map)
        def prolongedExperiment = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        def expectedActiveTo = ZonedDateTime.parse(startedExperiment.body.activityPeriod.activeTo).plusDays(30).toString()
        prolongedExperiment.body.activityPeriod.activeTo == expectedActiveTo

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/stop"), Map)

        then:
        assertThatExperimentWithId(request.id)
                .hasStatus(ExperimentStatus.ENDED)

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/pause"), Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST

        and:
        restTemplate.delete(localUrl("/api/admin/experiments/${request.id}"))

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND

        when:
        def changes = javers.findChanges(QueryBuilder.byInstanceId("some2", Experiment).build())

        //TODO clear
        println( javers.processChangeList(changes, new SimpleTextChangeLog()) )
        println( javers.getJsonConverter().toJson(changes) )

        then:
        changes
    }

    ExperimentResponseAssertions assertThatExperimentWithId(String id) {
        def response = fetchExperiment(id)
        return new ExperimentResponseAssertions(response)
    }

    def fetchExperiment(String id) {
        return restTemplate.getForEntity(localUrl("/api/admin/experiments/$id/"), Map)
    }

    static class ExperimentResponseAssertions {
        ResponseEntity<Map> response

        ExperimentResponseAssertions(ResponseEntity<Map> response) {
            this.response = response
        }

        def hasStatus(ExperimentStatus experimentStatus) {
            assert response.body.status == experimentStatus.toString()
            return this
        }

    }
}

