package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.FileBasedExperimentsRepository

class ExperimentCommandsExperimentGroupE2ESpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentGroupRepository experimentGroupRepository

    @Autowired
    FileBasedExperimentsRepository fileBasedExperimentsRepository

    @Autowired
    ExperimentsRepository experimentsRepository

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def "should delete DRAFT experiment bound to group"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        restTemplate.delete(localUrl("/api/admin/experiments/${experimentId1}"))

        then:
        Map group = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body
        group.experiments == [experimentId2]

        when:
        restTemplate.getForEntity(localUrl("/api/admin/experiments/${experimentId1}/"), Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.NOT_FOUND
    }

    def "should not delete non DRAFT experiment bound to group"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        startExperiment(experimentId1)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        restTemplate.delete(localUrl("/api/admin/experiments/${experimentId1}"))

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should set experiment as list head when starting"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        startExperiment(experimentId2)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        startExperiment(experimentId1)

        then:
        Map group = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups/${groupId}"), Map).body
        group.experiments == [experimentId2, experimentId1]
    }

    // todo remove in next sprint
    def "should not change variants of experiment bound to group"() {
        given:
        userProvider.user = new User('Author', [], true)
        String groupId = UUID.randomUUID().toString()

        and:
        String experimentId1 = UUID.randomUUID().toString()
        String experimentId2 = UUID.randomUUID().toString()
        createDraftExperiment(experimentId1)
        createDraftExperiment(experimentId2)
        startExperiment(experimentId1)

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments/groups'), [
                id: groupId,
                experiments: [experimentId1, experimentId2]
        ], Map)

        when:
        restTemplate.put(localUrl("/api/admin/experiments/${experimentId1}/update-variants"),
                [
                        percentage         : 18,
                        variantNames       : ['a', 'b', 'c'],
                        internalVariantName: 'internV',
                        deviceClass        : 'phone'
                ], Map)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }
}
