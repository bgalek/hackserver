package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository

class CreatePairedExperimentE2ESpec extends BaseIntegrationSpec {
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

    def "should create paired experiment"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        String groupId = UUID.randomUUID().toString()

        createDraftExperiment(experimentId2)

        and:
        def pairedExperimentCreationRequest = [
                experimentCreationRequest: [
                        id                 : experimentId1,
                        description        : 'desc',
                        documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                        variantNames       : ['base', 'v2', 'v3'],
                        internalVariantName: 'v1',
                        percentage         : 10,
                        deviceClass        : 'phone',
                        groups             : ['group a', 'group b'],
                        reportingEnabled   : true,
                        reportingType: 'BACKEND'
                ],
                experimentGroupCreationRequest: [
                        id: groupId,
                        experiments: [experimentId1, experimentId2]
                ]
        ]

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/create-paired-experiment'), pairedExperimentCreationRequest, Map)

        then:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/${experimentId1}/"), Map).statusCodeValue == 200

        and:
        Map createdGroup = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body
        createdGroup.experiments == [experimentId1, experimentId2]
        createdGroup.id == groupId
    }

    def "should not create paired experiment if cannot create group"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        String experimentId1 = UUID.randomUUID().toString()
        String unknownExperimentId = UUID.randomUUID().toString()
        String groupId = UUID.randomUUID().toString()

        and:
        def pairedExperimentCreationRequest = [
                experimentCreationRequest: [
                        id                 : experimentId1,
                        description        : 'desc',
                        documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                        variantNames       : ['base', 'v2', 'v3'],
                        internalVariantName: 'v1',
                        percentage         : 10,
                        deviceClass        : 'phone',
                        groups             : ['group a', 'group b'],
                        reportingEnabled   : true,
                        reportingType: 'BACKEND'
                ],
                experimentGroupCreationRequest: [
                        id: groupId,
                        experiments: [experimentId1, unknownExperimentId]
                ]
        ]

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/create-paired-experiment'), pairedExperimentCreationRequest, Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/${experimentId1}/"), Map)

        then:
        ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body

        then:
        ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def "should not create paired experiment if cannot create experiment"() {
        given:
        userProvider.user = new User('Anonymous', [], true)

        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        String groupId = UUID.randomUUID().toString()

        createDraftExperiment(experimentId2)

        and:
        def pairedExperimentCreationRequest = [
                experimentCreationRequest: [
                        id                 : experimentId2,
                        description        : 'desc',
                        documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                        variantNames       : ['base', 'v2', 'v3'],
                        internalVariantName: 'v1',
                        percentage         : 10,
                        deviceClass        : 'phone',
                        groups             : ['group a', 'group b'],
                        reportingEnabled   : true,
                        reportingType: 'BACKEND'
                ],
                experimentGroupCreationRequest: [
                        id: groupId,
                        experiments: [experimentId1, experimentId2]
                ]
        ]

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/create-paired-experiment'), pairedExperimentCreationRequest, Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/${experimentId1}/"), Map)

        then:
        ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body

        then:
        ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }
}
