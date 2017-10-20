package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.util.concurrent.ListenableFutureCallback
import java.util.concurrent.CompletableFuture

class ToCompletableFutureCallback<RESULT>(private val completableFuture: CompletableFuture<RESULT>) : ListenableFutureCallback<RESULT> {

    override fun onFailure(ex: Throwable) {
        completableFuture.completeExceptionally(ex)
    }

    override fun onSuccess(result: RESULT) {
        completableFuture.complete(result)
    }
}