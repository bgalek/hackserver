package pl.allegro.experiments.chi.chiserver.analytics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec

import java.time.ZonedDateTime

class AnalyticsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryEventEmitter inMemoryEventEmitter

    RestTemplate restTemplate = new RestTemplate()

    def "should emit experiment assignments"() {
        given:
            List<ExperimentAssignment> experimentAssignments = [
                sampleExperimentAssignment('user-1', 'variant-a'),
                sampleExperimentAssignment('user-2', 'variant-b')
            ]

        when:
            restTemplate.exchange(localUrl('/api/analytics'), HttpMethod.POST, new HttpEntity(experimentAssignments), Void.class)

        then:
            inMemoryEventEmitter.assertEventEmitted(experimentAssignments[0])
            inMemoryEventEmitter.assertEventEmitted(experimentAssignments[1])
    }


    ExperimentAssignment sampleExperimentAssignment(String userId, String variantName) {
        return new ExperimentAssignment(
                userId,
                null,
                'experimentId',
                variantName,
                false,
                true,
                'iphone',
                ZonedDateTime.now())
    }
}
