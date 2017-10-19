package pl.allegro.experiments.chi.chiserver.assignments

import groovy.transform.CompileStatic

import java.util.concurrent.CompletableFuture

@CompileStatic
class InMemoryEventEmitter implements EventEmitter {

    private final List<ExperimentAssignmentEvent> experimentAssignments = []

    @Override
    CompletableFuture<Boolean> emit(ExperimentAssignmentEvent experimentAssignment) {
        experimentAssignments.add(experimentAssignment)
        return CompletableFuture.completedFuture(true)
    }

    boolean assertEventEmitted(ExperimentAssignmentEvent experimentAssignment) {
        return experimentAssignments.find { experimentAssignment } != null
    }
}
