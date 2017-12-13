package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import com.github.salomonbrys.kotson.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.statistics.*
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DruidStatisticsRepository(val druidApiHost: String, val datasource: String, val restTemplate: RestTemplate,
                                val jsonConverter: JsonConverter) : StatisticsRepository {

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
        """.let { druidQuery(it) }
            .let { jsonConverter.fromJson(it).array }
            .let { if (it.size() > 0) it[0] else null }
            ?.let { it["result"]["maxTime"].string }
            ?.let { it.substring(0, 10) }
            ?.let { LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")) }

    override fun experimentStatistics(experiment: Experiment, toDate: LocalDate, device: String): ExperimentStatistics {
        val dateStr = DateTimeFormatter.ISO_LOCAL_DATE.format(toDate)
        val query = """
            {
              "queryType": "groupBy",
              "dataSource": "$datasource",
              "intervals": "${dateStr}T00Z/${dateStr}T23:59:00.000Z",
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

        val druidRespone = druidQuery(query)

        return parseStatistics(druidRespone, experiment.id, toDate, device)
    }

    private fun parseStatistics(druidResponse: String, experimentId: ExperimentId, toDate: LocalDate, device: String):
        ExperimentStatistics {
        var duration = 0L

        val stats = jsonConverter.fromJson(druidResponse).asJsonArray
        val metrics = stats.fold(mutableMapOf<MetricName, MutableMap<VariantName, VariantStatistics>>(), { m, e ->
            val metricName = e["event"]["metric"].string
            val variantName = e["event"]["experiment_variant"].string
            duration = maxOf(duration, e["event"]["sum_experiment_duration"].long)

            val variants = m.getOrDefault(metricName, mutableMapOf<VariantName, VariantStatistics>())

            variants[variantName] = VariantStatistics(
                value = e["event"]["sum_metric_value"].double,
                diff = e["event"]["sum_metric_value_diff"].double,
                pValue = e["event"]["sum_p_value"].double,
                count = e["event"]["count"].int
            )

            m[metricName] = variants

            m
        })

        return ExperimentStatistics(experimentId, toDate, Duration.ofMillis(duration), device, metrics)
    }

    private fun druidQuery(body: String): String {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val query = HttpEntity<String>(body, headers)
        return restTemplate.postForEntity("http://${druidApiHost}/druid/v2/?pretty",
            query, String::class.java).body
    }
}
