package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.test.annotation.DirtiesContext
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository
import spock.lang.Unroll

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

    @Unroll
    def "should set editable flag depending on who ask for single experiment/multiple experiments"() {
        given:
        userProvider.user = new User('Author', [], true)
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
                id                 : 'some2',
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : ['v2', 'v3'],
                internalVariantName: 'v1',
                percentage         : 10,
                deviceClass        : 'phone',
                groups             : ['group a', 'group b'],
                reportingEnabled   : true
        ]

        when:
        def response = restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        then:
        response.statusCode == HttpStatus.CREATED

        and:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List)
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map)

        then:
        def expectedExperiment = [
                id              : 'some2',
                author          : 'Anonymous',
                status          : 'DRAFT',
                measurements    : [lastDayVisits: 0],
                editable        : true,
                groups          : ['group a', 'group b'],
                origin          : 'MONGO',
                description     : 'desc',
                documentLink    : 'https://vuetifyjs.com/vuetify/quick-start',
                reportingEnabled: true,
                reportingType   : 'BACKEND',
                eventDefinitions: [],
                renderedVariants        : [
                        [
                                name      : 'v1',
                                predicates: [[type: 'INTERNAL']]
                        ],
                        [
                                name      : 'v2',
                                predicates: [[type: 'HASH', from: 0, to: 10], [type: 'DEVICE_CLASS', device: 'phone']]
                        ],
                        [
                                name      : 'v3',
                                predicates: [[type: 'HASH', from: 50, to: 60], [type: 'DEVICE_CLASS', device: 'phone']]
                        ]
                ],
                variantNames       : ['v2', 'v3'],
                internalVariantName: 'v1',
                deviceClass        : 'phone',
                percentage         : 10
        ]
        responseSingle.body.definition == expectedExperiment.definition
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
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/update-descriptions"),
                [
                        description : 'chi rulez',
                        documentLink: 'new link',
                        groups      : ['group c'],
                ], Map)

        then:
        def e = fetchExperiment(request.id).body
        e.description == 'chi rulez'
        e.documentLink == 'new link'
        e.groups == ['group c']

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${request.id}/update-variants"),
                [
                        percentage         : 18,
                        variantNames       : ['a', 'b', 'c'],
                        internalVariantName: 'internV',
                        deviceClass        : 'phone'
                ], Map)

        then:
        def definition = fetchExperiment(request.id).body
        definition.percentage == 18
        definition.internalVariantName == 'internV'
        definition.variantNames == ['a', 'b', 'c']
        definition.deviceClass == 'phone'

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

