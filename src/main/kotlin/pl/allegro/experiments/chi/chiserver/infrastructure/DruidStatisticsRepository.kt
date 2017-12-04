package pl.allegro.experiments.chi.chiserver.infrastructure

import com.github.salomonbrys.kotson.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.statistics.*
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DruidStatisticsRepository(val druidApiHost : String, val restTemplate: RestTemplate,
                                val jsonConverter: JsonConverter) : StatisticsRepository {
    override fun experimentStatistics(experimentId: ExperimentId, toDate: LocalDate, device: String) : ExperimentStatistics {
        val jsonUrl = "http://${druidApiHost}/druid/v2/?pretty"
        val dateStr = DateTimeFormatter.ISO_LOCAL_DATE.format(toDate)
        val json = """
            {
              "queryType": "groupBy",
              "dataSource": "chi_stats_beta",
              "intervals": "${dateStr}T00Z/${dateStr}T23:59:59.999Z",
              "granularity": "all",
              "filter": {
                "type": "and",
                "fields": [
                  {
                    "type": "selector",
                    "dimension": "experiment_id",
                    "value": "$experimentId"
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

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON_UTF8
        val query = HttpEntity<String>(json, headers)
        val result = restTemplate.postForEntity(jsonUrl, query, String::class.java).body
        var duration = 0L

        val stats = jsonConverter.fromJson(result).asJsonArray
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
}