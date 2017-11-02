package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.PeriodicTrigger
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BufferedAssignmentFlushSchedulerConfig(
        private val repository: BufferedAssignmentRepository,
        @Value("\${assignments.flush-period}") private val flushPeriod: Long) {

    private val scheduler: ThreadPoolTaskScheduler = ThreadPoolTaskScheduler()

    init {
        scheduler.threadNamePrefix = "flush-scheduler-"
    }

    @PostConstruct
    fun init() {
        this.scheduler.initialize()
        this.scheduler.schedule(Runnable { repository.flush() }, PeriodicTrigger(flushPeriod))
    }
}