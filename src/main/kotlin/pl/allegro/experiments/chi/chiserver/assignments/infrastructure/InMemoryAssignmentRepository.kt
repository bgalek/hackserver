package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.Assignment
import pl.allegro.experiments.chi.chiserver.assignments.AssignmentRepository
import java.util.*

class InMemoryAssignmentRepository : AssignmentRepository {
    private val assignments: MutableList<Assignment> = LinkedList()

    override fun save(assignment: Assignment) {
        assignments.add(assignment)
    }

    fun assertAssignmentSaved(assignment: Assignment): Boolean {
        return assignments.contains(assignment)
    }
}