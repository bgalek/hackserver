package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository

class ExperimentGroupE2ESpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def "should create experiment group"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: ['e1', 'e2']
        ], Map)

        then:
        Map createdGroup = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body
        createdGroup.experiments == ['e1', 'e2']
        createdGroup.id == groupId
    }

    def "should not create experiment group if contains more than 1 active experiment"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createStartedExperiment(experimentId1)
        createStartedExperiment(experimentId2)

        when:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should not create experiment group if one of provided experiment names does not exist"() {

    }

    def "should not create experiment group if group name is not unique"() {

    }

    def "should not create experiment group if there is no enough percentage space"() {

    }

    def "should not create experiment group if one of experiments is from stash"() {

    }

    def createStartedExperiment(String experimentId) {
        def request = [
                id                 : experimentId,
                description        : 'desc',
                documentLink       : 'https://vuetifyjs.com/vuetify/quick-start',
                variantNames       : ['v2', 'v3'],
                internalVariantName: 'v1',
                percentage         : 10,
                deviceClass        : 'phone',
                groups             : ['group a', 'group b'],
                reportingEnabled   : true,
                reportingType: 'FRONTEND',
                eventDefinitions: []
        ]
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)
        def startRequest = [
                experimentDurationDays: 30
        ]
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId}/start"), startRequest, Map)
    }
}
