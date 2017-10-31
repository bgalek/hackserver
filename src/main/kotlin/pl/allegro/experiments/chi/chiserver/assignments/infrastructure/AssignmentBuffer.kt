package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.Assignment
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class AssignmentBuffer(private val maxSize: Int) {

    private val queue: ConcurrentLinkedQueue<Assignment> = ConcurrentLinkedQueue()

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

    fun add(assignment: Assignment) {
        synchronized(this) {
            if (queue.size >= maxSize) {
                throw BufferMaxSizeExceededError("Max size exceeded")
            }

            queue.add(assignment)
        }
    }

    fun flush(): List<Assignment> {
        synchronized(this) {
            val result: MutableList<Assignment> = LinkedList()
            while (!queue.isEmpty()) {
                result.add(queue.poll())
            }
            return result
        }
    }
}