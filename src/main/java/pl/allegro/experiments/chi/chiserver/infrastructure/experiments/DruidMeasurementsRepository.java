package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentMeasurements;
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidException;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DruidMeasurementsRepository implements MeasurementsRepository {
    private static final Logger logger = LoggerFactory.getLogger(DruidMeasurementsRepository.class);
    private static final int DRUID_QUERY_TIMEOUT_MS = 2000;
    private static final long CACHE_EXPIRE_DURATION_MINUTES = 30L;

    private final DruidClient druid;
    private final Gson jsonConverter;
    private final String datasource;
    private final Supplier<Map<String, Integer>> lastDayVisitsCache;

    DruidMeasurementsRepository(
            DruidClient druid,
            Gson jsonConverter,
            String datasource) {
        this.druid = druid;
        this.jsonConverter = jsonConverter;
        this.datasource = datasource;
        this.lastDayVisitsCache = Suppliers.memoizeWithExpiration(() -> {
            try {
                return queryLastDayVisits();
            } catch (DruidException e) {
                logger.warn("Error while trying to query Druid for last day visits", e);
                return Collections.emptyMap();
            }
        }, CACHE_EXPIRE_DURATION_MINUTES, TimeUnit.MINUTES);
    }

    private ExperimentMeasurements createExperimentMeasurements(String expId, Map<String, Integer> lastDayVisits) {
        Integer lastDayVisitsNullable = lastDayVisits.get(expId);

        return new ExperimentMeasurements(lastDayVisitsNullable != null ? lastDayVisitsNullable : 0);
    }

    @Override
    public ExperimentMeasurements getMeasurements(String experimentId) {
        return new ExperimentMeasurements(lastDayVisitsCache.get().get(experimentId));
    }

    private Map<String, Integer> queryLastDayVisits() {
        String query = "{\n" +
                "            \"queryType\": \"topN\",\n" +
                "            \"dataSource\": \"" + this.datasource + "\",\n" +
                "            \"intervals\": \"" + DruidClient.lastDayIntervals() + "\",\n" +
                "            \"granularity\": \"all\",\n" +
                "            \"context\": {\n" +
                "            \"timeout\": " + DRUID_QUERY_TIMEOUT_MS +
                "        },\n" +
                "            \"dimension\": {\n" +
                "            \"type\": \"default\",\n" +
                "            \"dimension\": \"experiment_id\",\n" +
                "            \"outputName\": \"experiment_id\"\n" +
                "        },\n" +
                "            \"aggregations\": [\n" +
                "            {\n" +
                "                \"name\": \"sum_visit_count\",\n" +
                "                \"type\": \"doubleSum\",\n" +
                "                \"fieldName\": \"visit_count\"\n" +
                "            }\n" +
                "            ],\n" +
                "            \"metric\": \"sum_visit_count\",\n" +
                "            \"threshold\": 50\n" +
                "        }\n";
        return Optional.of(query)
                .map(it -> druid.query(it))
                .map(it -> jsonConverter.fromJson(it, JsonArray.class))
                .map(it -> it.size() > 0 ? it.get(0) : null)
                .map(it -> {
                    Map<String, Integer> result = new HashMap<>();
                    it.getAsJsonObject()
                            .get("result")
                            .getAsJsonArray()
                            .forEach(e -> result.put(e.getAsJsonObject().get("experiment_id").getAsString(),
                                    e.getAsJsonObject().get("sum_visit_count").getAsInt()));
                    return result;
                })
                .orElse(new HashMap<>());
    }
}
