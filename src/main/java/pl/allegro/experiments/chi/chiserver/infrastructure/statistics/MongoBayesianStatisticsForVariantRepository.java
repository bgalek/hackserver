package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import com.mongodb.client.result.DeleteResult;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsForVariantRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class MongoBayesianStatisticsForVariantRepository implements BayesianStatisticsForVariantRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoBayesianStatisticsForVariantRepository.class);

    private static final String COLLECTION = "bayesianExperimentStatistics";
    private static final Class ENTITY = BayesianExperimentStatisticsForVariant.class;

    private final MongoTemplate mongoTemplate;
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;

    @Autowired
    MongoBayesianStatisticsForVariantRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        this.mongoTemplate = mongoTemplate;
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
    }

    @Override
    public List<BayesianExperimentStatisticsForVariant> getLatestForAllVariants(String experimentId) {
        Timer timer = experimentsMongoMetricsReporter.timerReadBayesianExperimentStatistics();
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("experimentId").is(experimentId));

            List<BayesianExperimentStatisticsForVariant> records =
                    (List)(timer.wrap(() -> mongoTemplate.find(query, ENTITY, COLLECTION)).call());

            Map<String, Optional<BayesianExperimentStatisticsForVariant>> maxDateByMetric = records
                    .stream()
                    .collect(Collectors.groupingBy(BayesianExperimentStatisticsForVariant::getMetricName,
                             Collectors.maxBy(Comparator.comparing(BayesianExperimentStatisticsForVariant::getToDate))));

            //return only latest stats for each metric
            return records.stream()
                    .filter(r -> r.getToDate().equals(maxDateByMetric.get(r.getMetricName()).get().getToDate()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(BayesianExperimentStatisticsForVariant newStats) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteBayesianExperimentStatistics();
        timer.record(() -> {
            removeOldStats(newStats);

            logger.info("Saving new Bayesian stats for: {} {} {} {} {}", newStats.getExperimentId(), newStats.getMetricName(), newStats.getDevice(), newStats.getVariantName(), newStats.getToDate());
            mongoTemplate.save(newStats, COLLECTION);
        });
    }

    private void removeOldStats(BayesianExperimentStatisticsForVariant newStats) {
        Query delete = new Query();

        delete.addCriteria(Criteria.where("experimentId").is(newStats.getExperimentId()));
        delete.addCriteria(Criteria.where("device").is(newStats.getDevice()));
        delete.addCriteria(Criteria.where("variantName").is(newStats.getVariantName()));
        delete.addCriteria(Criteria.where("toDate").lte(newStats.getToDate()));
        delete.addCriteria(Criteria.where("metricName").is(newStats.getMetricName()));

        DeleteResult remove = mongoTemplate.remove(delete, ENTITY, COLLECTION);
        logger.info("Removed {} old Bayesian stats for: {} {} {} {} {}", remove.getDeletedCount(), newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getToDate(), newStats.getMetricName());
    }
}
