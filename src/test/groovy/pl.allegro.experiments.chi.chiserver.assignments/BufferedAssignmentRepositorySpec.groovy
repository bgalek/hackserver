package pl.allegro.experiments.chi.chiserver.assignments

import com.codahale.metrics.MetricRegistry
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.AssignmentBuffer
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.BufferedAssignmentRepository
import pl.allegro.experiments.chi.chiserver.assignments.infrastructure.InMemoryAssignmentRepository
import spock.lang.Specification

import java.time.Instant

class BufferedAssignmentRepositorySpec extends Specification {

    InMemoryAssignmentRepository repository = new InMemoryAssignmentRepository()

    AssignmentBuffer buffer = new AssignmentBuffer(3)

    BufferedAssignmentRepository bufferedAssignmentRepository = new BufferedAssignmentRepository(new MetricRegistry(), buffer, repository)

    def cleanup() {
        buffer.flush()
        repository.assignments.clear()
    }

    def "should add assignment to buffer when saving"() {
        given: "sample assignment"
        def assignment = sampleAssignment()

        when: "we save assignment"
        bufferedAssignmentRepository.save(assignment)

        then: "assignment is added to buffer"
        buffer.flush() as Set == [assignment] as Set
    }

    def "should save all assignments from buffer when flushing"() {
        given: "sample assignment"
        def assignment = sampleAssignment()

        and: "saved in buffer"
        bufferedAssignmentRepository.save(assignment)

        when: "we call flush"
        bufferedAssignmentRepository.flush()

        then: "assignment is saved in repo"
        repository.assertAssignmentSaved(assignment)
    }

    def "should drop assignments when buffer is overloaded and saving"() {
        given: "there is buffered repository with full buffer"
        (1..buffer.maxSize).forEach {bufferedAssignmentRepository.save(sampleAssignment())}

        when: "we try to save assignment"
        def newAssignment = sampleAssignment()
        bufferedAssignmentRepository.save(newAssignment)

        then: "buffer is flushed"
        buffer.flush() as Set == [newAssignment] as Set
    }

    private static Assignment sampleAssignment() {
        new Assignment("userId123", "cmid123", "experimentId123",
                "variant134", false, true, "iphone", Instant.now())
    }
}
