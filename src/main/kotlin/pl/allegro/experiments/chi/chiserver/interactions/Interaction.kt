package pl.allegro.experiments.chi.chiserver.interactions

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class Interaction(
        val userId: String?,
        val userCmId: String?,
        val experimentId: String,
        val variantName: String,
        val internal: Boolean?,
        val deviceClass: String?,
        val interactionDate: Instant,
        @JsonIgnore val appId : String?
)