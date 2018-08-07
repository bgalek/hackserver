package pl.allegro.experiments.chi.chiserver.infrastructure.experiments.fetch

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.application.administration.CustomParameterSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import spock.lang.Unroll

class ClientExperimentsV4E2ESpec extends BaseIntegrationSpec {

    RestTemplate restTemplate = new RestTemplate()

    @Autowired
    UserProvider userProvider

    def setup() {
        userProvider.user = new User('Author', [], true)
    }

    @Unroll
    def "should ignore experiment with customParam in client API #apiVersion"() {
        given:
        String expId = UUID.randomUUID()
        createCustomParamExperiment(expId)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/$apiVersion"), List)

        then:
        !response.body.find{it.id == expId}

        where:
        apiVersion << ["v1","v2","v3"]
    }

    @Unroll
    def "should serve experiment with customParam in client API #apiDesc"() {
        given:
        String expId = UUID.randomUUID()
        createCustomParamExperiment(expId)

        when:
        def response = restTemplate.getForEntity(localUrl("/api/experiments/$apiVersion"), List)

        then:
        response.body.find{it.id == expId}

        where:
        apiVersion << ["v4", ""]
        apiDesc <<    ["v4", "latest"]
    }

    void createCustomParamExperiment(expId) {
        userProvider.user = new User('Author', [], true)
        def request = CustomParameterSpec.requestExperimentWithCustomParam(expId)
        restTemplate.postForEntity(localUrl('/api/admin/experiments/'), request, Map)
    }
}