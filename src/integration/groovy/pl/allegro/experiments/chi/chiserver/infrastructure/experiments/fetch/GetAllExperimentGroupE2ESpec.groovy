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
        String experimentId1 = createDraftExperiment()
        String experimentId2 = createDraftExperiment()
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
                salt: experimentId1,
                experiments: [experimentId1, experimentId2]
        ]
    }
}
