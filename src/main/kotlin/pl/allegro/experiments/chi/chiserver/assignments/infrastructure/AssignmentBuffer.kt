package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.ExperimentAssignment
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class AssignmentBuffer(private val maxSize: Int) {

    private val queue: ConcurrentLinkedQueue<ExperimentAssignment> = ConcurrentLinkedQueue()

    fun isFull(): Boolean {
        synchronized(this) {
            return queue.size >= maxSize
        }
    }

    fun isEmpty(): Boolean {
        synchronized(this) {
            return queue.isEmpty()
        }
    }

    fun add(experimentAssignment: ExperimentAssignment) {
        synchronized(this) {
            if (queue.size >= maxSize) {
                throw BufferMaxSizeExceededError("Max size exceeded")
            }

            queue.add(experimentAssignment)
        }
    }

    fun flush(): List<ExperimentAssignment> {
        synchronized(this) {
            val result: MutableList<ExperimentAssignment> = LinkedList()
            while (!queue.isEmpty()) {
                result.add(queue.poll())
            }
            return result
        }
    }
}