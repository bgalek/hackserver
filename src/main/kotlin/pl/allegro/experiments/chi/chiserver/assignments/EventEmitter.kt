package pl.allegro.experiments.chi.chiserver.assignments

import java.util.concurrent.CompletableFuture

interface EventEmitter {
    fun emit(experimentAssignment: ExperimentAssignmentEvent): CompletableFuture<Boolean>
}