package pl.allegro.experiments.chi.chiserver.analytics.infrastructure

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "analytics.hermes")
data class HermesTopicProperties(
        val topic: String = "pl.allegro.experiments.chi.assignments",
        val schemaVersion: Int = 1
)