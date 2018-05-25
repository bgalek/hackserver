package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider

class GetAllExperimentGroupE2ESpec extends BaseIntegrationSpec {
    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def "should return all experiment groups"() {
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
        def allGroups = restTemplate.getForEntity(localUrl("/api/admin/experiments/groups"), List).body

        then:
        allGroups.find({g -> g.id == groupId}) == [
                id: groupId,
                nameSpace: experimentId1,
                experiments: [experimentId1, experimentId2]
        ]
    }
}
