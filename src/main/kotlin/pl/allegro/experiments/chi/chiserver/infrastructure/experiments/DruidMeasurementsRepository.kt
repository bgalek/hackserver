package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.github.salomonbrys.kotson.*
import com.google.common.base.Suppliers
import com.google.gson.JsonArray
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentMeasurements
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidException
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.lastDayIntervals
import pl.allegro.experiments.chi.chiserver.logger
import java.util.concurrent.TimeUnit

private const val DRUID_QUERY_TIMEOUT_MS = 2000
private const val CACHE_EXPIRE_DURATION_MINUTES = 30L

class DruidMeasurementsRepository(private val druid: DruidClient,
                                  private val jsonConverter: JsonConverter,
                                  private val datasource: String) : MeasurementsRepository {

    private var lastDayVisits = emptyMap<String, Int>()

    companion object {
        private val logger by logger()
    }

    private val lastDayVisitsCache = Suppliers.memoizeWithExpiration(
        {
            try {
                lastDayVisits = queryLastDayVisits()
            } catch (e: DruidException) {
                logger.warn("Error while trying to query Druid for last day visits", e)
            }
            lastDayVisits
        },
        CACHE_EXPIRE_DURATION_MINUTES, TimeUnit.MINUTES)

    private fun asMeasuredExperiment(ex: Experiment, lastDayVisits: Map<String, Int> = this.lastDayVisits) =
        with(ex) {
            Experiment(id, variants, description, documentLink, author, groups, reportingEnabled, activityPeriod,
                ExperimentMeasurements(lastDayVisits[id] ?: 0), editable, null, ex.getExplicitStatus().orElse(null))
        }

    override fun withMeasurements(experiment: Experiment): Experiment = asMeasuredExperiment(experiment)

    override fun withMeasurements(experiments: List<Experiment>): List<Experiment> {
        val lastDayVisits = lastDayVisitsCache.get()
        return experiments.map { asMeasuredExperiment(it, lastDayVisits) }
    }

    private fun queryLastDayVisits(): Map<String, Int> =
        """{
            "queryType": "topN",
            "dataSource": "$datasource",
            "intervals": "${lastDayIntervals()}",
            "granularity": "all",
            "context": {
            "timeout": $DRUID_QUERY_TIMEOUT_MS
        },
            "dimension": {
            "type": "default",
            "dimension": "experiment_id",
            "outputName": "experiment_id"
        },
            "aggregations": [
            {
                "name": "sum_visit_count",
                "type": "doubleSum",
                "fieldName": "visit_count"
            }
            ],
            "metric": "sum_visit_count",
            "threshold": 50
        }
        """.let { druid.query(it) }
            .let { jsonConverter.fromJson<JsonArray>(it) }
            .let { if (it.size() > 0) it[0] else null }
            ?.let {
                it["result"].array.associateBy(
                    { it["experiment_id"].string },
                    { it["sum_visit_count"].int })
            }
            ?: emptyMap()
}
