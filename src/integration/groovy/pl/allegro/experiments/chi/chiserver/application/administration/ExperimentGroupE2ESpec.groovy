package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup
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
        ExperimentGroup createdGroup = experimentGroupRepository.get(groupId).get()
        createdGroup.experiments == ['e1', 'e2']
        createdGroup.id == groupId
        createdGroup.nameSpace != null
    }

    def "should not create experiment group if contains more than 1 active experiment"() {

    }

    def "should not create experiment group if group name is not unique"() {

    }

    def "should not create experiment if there is no enough percentage space"() {

    }

}
