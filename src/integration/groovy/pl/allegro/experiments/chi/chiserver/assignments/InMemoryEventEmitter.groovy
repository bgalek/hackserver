package pl.allegro.experiments.chi.chiserver.assignments

import groovy.transform.CompileStatic

import java.util.concurrent.CompletableFuture

@CompileStatic
class InMemoryEventEmitter implements ExperimentAssignmentRepository {

    private final List<ExperimentAssignment> experimentAssignments = []

    @Override
    CompletableFuture<Object> save(ExperimentAssignment experimentAssignment) {
        experimentAssignments.add(experimentAssignment)
        return CompletableFuture.completedFuture(null)
    }

    boolean assertEventEmitted(ExperimentAssignment experimentAssignment) {
        return experimentAssignments.find { experimentAssignment } != null
    }
}
