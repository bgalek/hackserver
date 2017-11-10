package pl.allegro.experiments.chi.chiserver.interactions

import com.codahale.metrics.MetricRegistry
import com.github.slugify.Slugify
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader

class InteractionsMetricsReporter(private val metricRegistry : MetricRegistry) {

    private val RECEIVED_INTERACTIONS = "interactions.received"

    val slugify = Slugify()

    val slugCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .build(
                    object : CacheLoader<String, String>() {
                        override fun load(text: String): String {
                            return slugify.slugify(text)
                        }
                    })

    fun meter(interactions : List<Interaction>) {
        metricRegistry.meter(RECEIVED_INTERACTIONS+".all").mark(interactions.size.toLong())

        interactions.forEach {
            val appId = slugCache.get(it.appId ?: "")
            val experiment = slugCache.get(it.experimentId + "-" + it.variantName)
            metricRegistry.meter(RECEIVED_INTERACTIONS + "." + appId + "." + experiment).mark()
        }
    }
}
