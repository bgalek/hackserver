package pl.allegro.experiments.chi.chiserver.interactions.infrastructure

import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.PeriodicTrigger
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class BufferedInteractionFlushSchedulerConfig(
        private val repository: BufferedInteractionRepository,
        @Value("\${interactions.flush-period}") private val flushPeriod: Long) {

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