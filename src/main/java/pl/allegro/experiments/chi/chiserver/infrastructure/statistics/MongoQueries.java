package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.common.collect.Iterators;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Arrays;

public class MongoQueries {

    public static int countDistinctKeys(MongoTemplate mongoTemplate, Timer timer, String collection, String key) {
        try {
            return timer.wrap(() -> {
                var aggregation = mongoTemplate.getCollection(collection).aggregate(
                        Arrays.asList(
                                Aggregates.group("$" + key, Accumulators.sum("count", 1))
                        ));
                return Iterators.size(aggregation.iterator());
            }).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
