package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "assignments.hermes")
data class HermesTopicProperties(
        var topic: String = "pl.allegro.experiments.chi.assignments",
        var schemaVersion: Int = 1
)