package pl.allegro.experiments.chi.chiserver.analytics

import java.util.concurrent.CompletableFuture

interface EventEmitter {
    fun emit(experimentAssignment: ExperimentAssignmentEvent): CompletableFuture<Boolean>
}