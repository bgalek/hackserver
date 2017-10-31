package pl.allegro.experiments.chi.chiserver.assignments

import groovy.transform.CompileStatic
import kotlin.Unit

import java.util.concurrent.CompletableFuture

@CompileStatic
class InMemoryExperimentAssignmentRepository implements ExperimentAssignmentRepository {

    private final List<ExperimentAssignment> experimentAssignments = []

    @Override
    void save(ExperimentAssignment experimentAssignment) {
        experimentAssignments.add(experimentAssignment)
    }

    boolean assertEventEmitted(ExperimentAssignment experimentAssignment) {
        return experimentAssignments.find { experimentAssignment } != null
    }
}
