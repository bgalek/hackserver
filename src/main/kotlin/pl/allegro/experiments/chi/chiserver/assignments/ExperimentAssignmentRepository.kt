package pl.allegro.experiments.chi.chiserver.assignments

interface ExperimentAssignmentRepository {
    fun save(experimentAssignment: ExperimentAssignment)
}