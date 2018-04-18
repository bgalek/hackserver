package pl.allegro.experiments.chi.chiserver.application.administration

import com.mongodb.DBObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.test.context.ContextConfiguration
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.ReportingDefinition
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsDoubleRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ReportingDefinitionSerializer

@ContextConfiguration(classes = [TestConfig])
class LegacyExperimentReportingDefinitionE2ESpec extends BaseIntegrationSpec {

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

    def "should append default reporting type to legacy mongo experiments"() {
        given:
        userProvider.user = new User('Anonymous', [], true)
        def experimentId = 'legacyMongoExperiment'

        def request = [
                id              : experimentId,
                variantNames    : ['v2'],
                percentage      : 10
        ]

        and:
        restTemplate.postForEntity(localUrl('/api/admin/experiments'), request, Map)

        when:
        def responseList = restTemplate.getForEntity(localUrl("/api/admin/experiments"), List).body
        def responseSingle = restTemplate.getForEntity(localUrl("/api/admin/experiments/${request.id}/"), Map).body

        then:
        responseList.find { it['id'] == experimentId }['reportingType'] == 'BACKEND'
        responseList.find { it['id'] == experimentId }['eventDefinitions'] == []

        and:
        responseSingle['reportingType'] == 'BACKEND'
        responseSingle['eventDefinitions']  == []
    }

    static class TestReportingDefinitionSerializer extends ReportingDefinitionSerializer {
        TestReportingDefinitionSerializer() {
            super(null)
        }

        @Override
        DBObject convert(ReportingDefinition source) {
            return null
        }
    }

    @Configuration
    static class TestConfig {

        @Primary
        @Bean
        ReportingDefinitionSerializer reportingDefinitionSerializer() {
            return new TestReportingDefinitionSerializer()
        }
    }
}
