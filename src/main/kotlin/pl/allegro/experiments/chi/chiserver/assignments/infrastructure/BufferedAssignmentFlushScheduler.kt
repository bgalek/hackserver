package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.scheduling.support.PeriodicTrigger
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
@EnableConfigurationProperties(BufferedAssignmentFlushSchedulerProperties::class)
class BufferedAssignmentFlushScheduler(
        private val repository: BufferedAssignmentRepository,
        private val properties: BufferedAssignmentFlushSchedulerProperties) {

    private val scheduler: ThreadPoolTaskScheduler = ThreadPoolTaskScheduler()

    init {
        scheduler.threadNamePrefix = "flush-scheduler-"
    }

    @PostConstruct
    fun init() {
        this.scheduler.initialize()
        this.scheduler.schedule(Runnable { repository.flush() }, PeriodicTrigger(properties.flushPeriod))
    }
}