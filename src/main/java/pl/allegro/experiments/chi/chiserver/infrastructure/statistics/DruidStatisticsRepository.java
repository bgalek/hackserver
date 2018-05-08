package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import pl.allegro.experiments.chi.chiserver.domain.statistics.ExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.VariantStatistics;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static pl.allegro.experiments.chi.chiserver.infrastructure.statistics.DruidClient.oneDayIntervals;

public class DruidStatisticsRepository implements StatisticsRepository {
    private final DruidClient druid;
    private final String datasource;
    private final Gson jsonConverter;

    public DruidStatisticsRepository(
            DruidClient druid,
            String datasource,
            Gson jsonConverter) {
        this.druid = druid;
        this.datasource = datasource;
        this.jsonConverter = jsonConverter;
    }

    @Override
    public boolean hasAnyStatistics(String experimentId) {
        return lastStatisticsDate(experimentId) != null;
    }

    @Override
    public LocalDate lastStatisticsDate(String experimentId) {
        String queryBody = "{\n" +
                "\"queryType\": \"timeBoundary\",\n" +
                "          \"dataSource\": \"" + this.datasource + "\",\n" +
                "          \"bound\": \"maxTime\",\n" +
                "          \"filter\": {\n" +
                "            \"type\": \"selector\",\n" +
                "              \"dimension\": \"experiment_id\",\n" +
                "              \"value\": \"" + experimentId + "\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n";
        return Optional.of(queryBody)
                .map(druid::query)
                .map(it -> jsonConverter.fromJson(it, JsonArray.class))
                .map(it -> it.size() > 0 ? it.get(0) : null)
                .map(it -> it.getAsJsonObject().get("result").getAsJsonObject().get("maxTime").getAsString())
                .map(it -> it.substring(0, 10))
                .map(it -> LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .orElse(null);
    }

    @Override
    public ExperimentStatistics experimentStatistics(String experimentId, LocalDate toDate, String device) {
        String intervals = oneDayIntervals(toDate);
        String query = "\n" +
                "            {\n" +
                "              \"queryType\": \"groupBy\",\n" +
                "              \"dataSource\": \"" + this.datasource + "\",\n" +
                "              \"intervals\": \"" + intervals + "\",\n" +
                "              \"granularity\": \"all\",\n" +
                "              \"filter\": {\n" +
                "                \"type\": \"and\",\n" +
                "                \"fields\": [\n" +
                "                  {\n" +
                "                    \"type\": \"selector\",\n" +
                "                    \"dimension\": \"experiment_id\",\n" +
                "                    \"value\": \"" + experimentId + "\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"type\": \"selector\",\n" +
                "                    \"dimension\": \"device_class\",\n" +
                "                    \"value\": \"" + device + "\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              },\n" +
                "              \"dimensions\": [ \"metric\", \"experiment_variant\" ],\n" +
                "              \"aggregations\": [\n" +
                "                {\n" +
                "                  \"name\": \"count\",\n" +
                "                  \"type\": \"longSum\",\n" +
                "                  \"fieldName\": \"count\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"sum_experiment_duration\",\n" +
                "                  \"type\": \"longSum\",\n" +
                "                  \"fieldName\": \"experiment_duration\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"sum_metric_value\",\n" +
                "                  \"type\": \"doubleSum\",\n" +
                "                  \"fieldName\": \"metric_value\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"sum_metric_value_diff\",\n" +
                "                  \"type\": \"doubleSum\",\n" +
                "                  \"fieldName\": \"metric_value_diff\"\n" +
                "                },\n" +
                "                {\n" +
                "                  \"name\": \"sum_p_value\",\n" +
                "                  \"type\": \"doubleSum\",\n" +
                "                  \"fieldName\": \"p_value\"\n" +
                "                }\n" +
                "              ]\n" +
                "            }\n";
        String druidResponse = druid.query(query);
        return parseStatistics(druidResponse, experimentId, toDate, device);
    }

    private ExperimentStatistics parseStatistics(
            String druidResponse,
            String experimentId,
            LocalDate toDate,
            String device) {
        long duration = 0L;
        JsonArray stats = jsonConverter.fromJson(druidResponse, JsonArray.class);
        Map<String, Map<String, VariantStatistics>> metrics = new HashMap<>();
        for (JsonElement e : stats) {
            String metricName = e.getAsJsonObject().get("event").getAsJsonObject().get("metric").getAsString();
            String variantName = e.getAsJsonObject().get("event").getAsJsonObject().get("experiment_variant").getAsString();
            duration = Long.max(duration, e.getAsJsonObject().get("event").getAsJsonObject().get("sum_experiment_duration").getAsLong());
            Map<String, VariantStatistics> variants = metrics.getOrDefault(metricName, new HashMap<>());
            variants.put(variantName, new VariantStatistics(
                    e.getAsJsonObject().get("event").getAsJsonObject().get("sum_metric_value").getAsDouble(),
                    e.getAsJsonObject().get("event").getAsJsonObject().get("sum_metric_value_diff").getAsDouble(),
                    e.getAsJsonObject().get("event").getAsJsonObject().get("sum_p_value").getAsDouble(),
                    e.getAsJsonObject().get("event").getAsJsonObject().get("count").getAsInt()
            ));
            metrics.put(metricName, variants);
        }
        return new ExperimentStatistics(experimentId, toDate, Duration.ofMillis(duration), device, ImmutableMap.copyOf(metrics));
    }
}