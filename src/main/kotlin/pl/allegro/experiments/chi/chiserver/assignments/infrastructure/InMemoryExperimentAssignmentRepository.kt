package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignment
import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignmentRepository
import java.util.*

class InMemoryExperimentAssignmentRepository: ExperimentAssignmentRepository {
    private val experimentAssignments: MutableList<ExperimentAssignment> = LinkedList()

    override fun save(experimentAssignment: ExperimentAssignment) {
        experimentAssignments.add(experimentAssignment)
    }

    fun assertAssignmentSaved(experimentAssignment: ExperimentAssignment): Boolean {
        return experimentAssignments.contains(experimentAssignment)
    }
}