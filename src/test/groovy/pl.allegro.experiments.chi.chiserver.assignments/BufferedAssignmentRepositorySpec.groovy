package pl.allegro.experiments.chi.chiserver.assignments

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.AssignmentBuffer
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.BufferedAssignmentRepository
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.InMemoryAssignmentRepository
import spock.lang.Specification

import java.time.Instant

class BufferedAssignmentRepositorySpec extends Specification {

    InMemoryAssignmentRepository repository = new InMemoryAssignmentRepository()
    MetricRegistry metrics = new MetricRegistry()
    int bufferMaxSize = 3
    AssignmentBuffer buffer = new AssignmentBuffer(bufferMaxSize, metrics)
    BufferedAssignmentRepository bufferedAssignmentRepository = new BufferedAssignmentRepository(metrics, buffer, repository)

    def cleanup() {
        buffer.flush()
        repository.assignments.clear()
    }

    def "should save all assignments from buffer when flushing"() {
        given:
        def assignment = sampleAssignment()

        when:
        bufferedAssignmentRepository.save(assignment)

        then: "assignment is added to buffer"
        repository.assignments.isEmpty()

        when:
        bufferedAssignmentRepository.flush()

        then:
        repository.assertAssignmentSaved(assignment)
    }

    def "should drop assignments when buffer is overloaded and saving"() {
        given: "there is buffered repository with full buffer"
        bufferMaxSize.times { bufferedAssignmentRepository.save(sampleAssignment()) }

        when: "we try to save assignment"
        def newAssignment = sampleAssignment()
        bufferedAssignmentRepository.save(newAssignment)

        then: "buffered assignments are dropped"
        bufferedAssignmentRepository.flush()
        repository.assignments as Set == [newAssignment] as Set
    }

    Assignment sampleAssignment() {
        new Assignment("userId123", "cmid123", "experimentId123",
                "variant134", false, true, "iphone", Instant.now())
    }
}
