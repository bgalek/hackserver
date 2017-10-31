package pl.allegro.experiments.chi.chiserver.assignments

interface AssignmentRepository {
    fun save(assignment: Assignment)
}