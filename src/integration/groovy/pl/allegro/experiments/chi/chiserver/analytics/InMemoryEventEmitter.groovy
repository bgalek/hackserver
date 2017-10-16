package pl.allegro.experiments.chi.chiserver.analytics

import groovy.transform.CompileStatic

import java.util.concurrent.CompletableFuture

@CompileStatic
class InMemoryEventEmitter implements EventEmitter {

    private final List<ExperimentAssignment> experimentAssignments = []

    @Override
    CompletableFuture<Boolean> emit(ExperimentAssignment experimentAssignment) {
        experimentAssignments.add(experimentAssignment)
        return CompletableFuture.completedFuture(true)
    }

    boolean assertEventEmitted(ExperimentAssignment experimentAssignment) {
        return experimentAssignments.find { experimentAssignment } != null
    }
}
