package pl.allegro.experiments.chi.chiserver.domain

import javax.validation.constraints.NotNull

data class DomainObject(
        @NotNull val value: String,
        val ttl: Int? = 0
)
