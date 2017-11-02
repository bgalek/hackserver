package pl.allegro.experiments.chi.chiserver.assignments

import groovy.json.JsonOutput
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.InMemoryAssignmentRepository

import java.time.Instant

class AssignmentsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    InMemoryAssignmentRepository inMemoryAssignmentRepository

    RestTemplate restTemplate = new RestTemplate()

    def "should save experiment assignments"() {
        given:
        List<AssignmentDto> experimentAssignments = [
                sampleExperimentAssignment('user-1', 'variant-a'),
                sampleExperimentAssignment('user-2', 'variant-b')
        ]

        when:
        restTemplate.exchange(localUrl('/api/assignments'), HttpMethod.POST, new HttpEntity(new AssignmentsDto(experimentAssignments)), Void.class)

        then:
        inMemoryAssignmentRepository.assertAssignmentSaved(experimentAssignments[0].toEvent())
        inMemoryAssignmentRepository.assertAssignmentSaved(experimentAssignments[1].toEvent())
    }

    def "should throw error when argument is missing"() {
        given:
        String data = JsonOutput.toJson([
                "assignmentDtos": [
                        [
                                "userId": "123",
                                "userCmId": "123",
                                "experimentId": "q234",
                                "variantName": "123",
                                "internal": true,
                                "confirmed": true,
                                "deviceClass": "iphone"
                        ]
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/assignments'), HttpMethod.POST, new HttpEntity<String>(data), Void.class)

        then:
        thrown(HttpClientErrorException)
    }

    def "should throw error when required argument is null"() {
        given:
        String data = JsonOutput.toJson([
                "assignmentDtos": [
                        [
                                "userId": null,
                                "userCmId": null,
                                "experimentId": null,
                                "variantName": null,
                                "internal": null,
                                "confirmed": null,
                                "deviceClass": null,
                                "assignmentData": null
                        ]
                ]
        ])

        when:
        restTemplate.exchange(localUrl('/api/assignments'), HttpMethod.POST, new HttpEntity<String>(data), Void.class)

        then:
        thrown(HttpClientErrorException)
    }

    AssignmentDto sampleExperimentAssignment(String userId, String variantName) {
        new AssignmentDto(
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
