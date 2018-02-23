package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import com.github.salomonbrys.kotson.*
import com.google.gson.JsonArray
import pl.allegro.experiments.chi.chiserver.domain.statistics.*
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.oneDayIntervals
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DruidStatisticsRepository(val druid: DruidClient, val datasource: String,
                                val jsonConverter: JsonConverter) : StatisticsRepository {

    override fun hasAnyStatistics(experiment: Experiment): Boolean {
        return lastStatisticsDate(experiment) != null
    }

    override fun lastStatisticsDate(experiment: Experiment): LocalDate? =
        """{
          "queryType": "timeBoundary",
          "dataSource": "$datasource",
          "bound": "maxTime",
          "filter": {
            "type": "selector",
              "dimension": "experiment_id",
              "value": "${experiment.id}"
            }
          }
        }
        """.let { druid.query(it) }
            .let { jsonConverter.fromJson<JsonArray>(it) }
            .let { if (it.size() > 0) it[0] else null }
            ?.let { it["result"]["maxTime"].string }
            ?.let { it.substring(0, 10) }
            ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

    override fun experimentStatistics(experiment: Experiment, toDate: LocalDate, device: String): ExperimentStatistics {
        val intervals = oneDayIntervals(toDate)
        val query = """
            {
              "queryType": "groupBy",
              "dataSource": "$datasource",
              "intervals": "$intervals",
              "granularity": "all",
              "filter": {
                "type": "and",
                "fields": [
                  {
                    "type": "selector",
                    "dimension": "experiment_id",
                    "value": "${experiment.id}"
                  },
                  {
                    "type": "selector",
                    "dimension": "device_class",
                    "value": "$device"
                  }
                ]
              },
              "dimensions": [ "metric", "experiment_variant" ],
              "aggregations": [
                {
                  "name": "count",
                  "type": "longSum",
                  "fieldName": "count"
                },
                {
                  "name": "sum_experiment_duration",
                  "type": "longSum",
                  "fieldName": "experiment_duration"
                },
                {
                  "name": "sum_metric_value",
                  "type": "doubleSum",
                  "fieldName": "metric_value"
                },
                {
                  "name": "sum_metric_value_diff",
                  "type": "doubleSum",
                  "fieldName": "metric_value_diff"
                },
                {
                  "name": "sum_p_value",
                  "type": "doubleSum",
                  "fieldName": "p_value"
                }
              ]
            }
            """

        val druidRespone = druid.query(query)

        return parseStatistics(druidRespone, experiment.id, toDate, device)
    }

    private fun parseStatistics(druidResponse: String, experimentId: String, toDate: LocalDate, device: String):
        ExperimentStatistics {
        var duration = 0L

        val stats = jsonConverter.fromJson<JsonArray>(druidResponse)
        val metrics = stats.fold(mutableMapOf<MetricName, MutableMap<VariantName, VariantStatistics>>(), { m, e ->
            val metricName = e["event"]["metric"].string
            val variantName = e["event"]["experiment_variant"].string
            duration = maxOf(duration, e["event"]["sum_experiment_duration"].long)

            val variants = m.getOrDefault(metricName, mutableMapOf<VariantName, VariantStatistics>())

            variants[variantName] = VariantStatistics(
                e["event"]["sum_metric_value"].double,
                e["event"]["sum_metric_value_diff"].double,
                e["event"]["sum_p_value"].double,
                e["event"]["count"].int
            )

            m[metricName] = variants

            m
        })

        return ExperimentStatistics(experimentId, toDate, Duration.ofMillis(duration), device, metrics)
    }
}
