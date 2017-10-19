package pl.allegro.experiments.chi.chiserver.assignments

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec

import java.time.Instant

class AssignmentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryEventEmitter inMemoryEventEmitter

    RestTemplate restTemplate = new RestTemplate()

    def "should emit experiment assignments"() {
        given:
            List<ExperimentAssignmentDto> experimentAssignments = [
                sampleExperimentAssignment('user-1', 'variant-a'),
                sampleExperimentAssignment('user-2', 'variant-b')
            ]

        when:
            restTemplate.exchange(localUrl('/api/assignments'), HttpMethod.POST, new HttpEntity(new ExperimentAssignmentsDto(experimentAssignments)), Void.class)

        then:
            inMemoryEventEmitter.assertEventEmitted(experimentAssignments[0].toEvent())
            inMemoryEventEmitter.assertEventEmitted(experimentAssignments[1].toEvent())
    }


    ExperimentAssignmentDto sampleExperimentAssignment(String userId, String variantName) {
        return new ExperimentAssignmentDto(
                userId,
                null,
                'experimentId',
                variantName,
                false,
                true,
                'iphone',
                Instant.now())
    }
}
