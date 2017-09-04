package pl.allegro.tech.selfservice.newservicesingle.domain

import javax.validation.constraints.NotNull

data class DomainObject(
        @NotNull val value: String,
        val ttl: Int? = 0
)
