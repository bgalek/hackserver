package pl.allegro.experiments.chi.chiserver.infrastructure.interactions

import com.codahale.metrics.MetricRegistry
import com.github.slugify.Slugify
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction

class InteractionsMetricsReporter(private val metricRegistry : MetricRegistry) {

    private val RECEIVED_INTERACTIONS = "interactions.received"

    private val IGNORED_INTERACTIONS = "interactions.ignored"

    val slugify = Slugify()

    val slugCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(
                    object : CacheLoader<String, String>() {
                        override fun load(text: String): String {
                            return slugify.slugify(text)
                        }
                    })

    fun meterIgnored(ignored : Int) {
        if (ignored > 0) {
            metricRegistry.meter(IGNORED_INTERACTIONS).mark(ignored.toLong())
        }
    }

    fun meterReceived(interactions : List<Interaction>) {
        metricRegistry.meter(RECEIVED_INTERACTIONS+".all").mark(interactions.size.toLong())

        interactions.forEach {
            val appId = slugCache.get(it.appId ?: "")
            val experiment = slugCache.get(it.experimentId + "-" + it.variantName)
            metricRegistry.meter(RECEIVED_INTERACTIONS + "." + appId + "." + experiment).mark()
        }
    }
}
