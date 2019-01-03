package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.google.common.collect.Iterators;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import io.micrometer.core.instrument.Timer;
import org.bson.conversions.Bson;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;

public class MongoQueries {

    public static int countDistinctKeys(MongoTemplate mongoTemplate, Timer timer, String collection, String key, Bson filter) {
        try {
            return timer.wrap(() -> {
                var aggregation = mongoTemplate.getCollection(collection).aggregate(
                        Arrays.asList(
                                Aggregates.match(filter),
                                Aggregates.group("$" + key, Accumulators.sum("count", 1))
                        ));
                return Iterators.size(aggregation.iterator());
            }).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T findMax(MongoTemplate mongoTemplate, Timer timer, String key, Class<T> entity, String collection) {
        try {
            return timer.wrap(() -> {
                Query query = new Query();
                query.with(new Sort(Sort.Direction.DESC, key));
                query.limit(1);
                return mongoTemplate.findOne(query, entity, collection);
            }).call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
