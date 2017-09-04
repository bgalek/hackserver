package pl.allegro.tech.selfservice.newservicesingle.domain

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("domain.properties")
data class DomainConfiguration(
    var value: String? = null
)