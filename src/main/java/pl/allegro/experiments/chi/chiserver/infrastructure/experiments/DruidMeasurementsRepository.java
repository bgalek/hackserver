package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentMeasurements;
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DruidMeasurementsRepository implements MeasurementsRepository {
    private static final Logger logger = LoggerFactory.getLogger(DruidMeasurementsRepository.class);
    private static final int DRUID_QUERY_TIMEOUT_MS = 2000;
    private static final long CACHE_EXPIRE_DURATION_MINUTES = 30L;

    private final DruidClient druid;
    private final Gson jsonConverter;
    private final String datasource;
    private Map<String, Integer> lastDayVisits;
    private final Supplier<Map<String, Integer>> lastDayVisitsCache;

    DruidMeasurementsRepository(
            DruidClient druid,
            Gson jsonConverter,
            String datasource) {
        this.druid = druid;
        this.jsonConverter = jsonConverter;
        this.datasource = datasource;
        this.lastDayVisits = new HashMap<>();
        this.lastDayVisitsCache = Suppliers.memoizeWithExpiration(new Supplier<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> get() {
                try {
                    lastDayVisits = queryLastDayVisits();
                } catch (DruidException e) {
                    logger.warn("Error while trying to query Druid for last day visits", e);
                }
                return lastDayVisits;
            }
        }, CACHE_EXPIRE_DURATION_MINUTES, TimeUnit.MINUTES);
    }

    private Experiment asMeasuredExperiment(Experiment experiment, Map<String, Integer> lastDayVisits) {
        if (lastDayVisits == null) {
            lastDayVisits = this.lastDayVisits;
        }
        Integer lastDayVisitsNullable = lastDayVisits.get(experiment.getId());
        ExperimentMeasurements measurements = new ExperimentMeasurements(lastDayVisitsNullable != null ? lastDayVisitsNullable : 0);
        return experiment.mutate()
                .measurements(measurements)
                .origin(null)
                .build();
    }

    @Override
    public Experiment withMeasurements(Experiment experiment) {
        return asMeasuredExperiment(experiment, null);
    }

    @Override
    public List<Experiment> withMeasurements(List<Experiment> experiments) {
        Map<String, Integer> lastDayVisits = lastDayVisitsCache.get();
        return experiments.stream()
                .map(it -> asMeasuredExperiment(it, lastDayVisits))
                .collect(Collectors.toList());
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
