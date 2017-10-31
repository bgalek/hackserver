package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("assignments")
class BufferedAssignmentFlushSchedulerProperties {
    var flushPeriod: Long = 500
}