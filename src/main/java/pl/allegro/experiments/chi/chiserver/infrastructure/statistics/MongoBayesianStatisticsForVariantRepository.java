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
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClass;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianStatisticsForVariantRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

import java.util.List;

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
    public List<BayesianExperimentStatisticsForVariant> getLatestForAllVariants(String experimentId, String device) {
        Timer timer = experimentsMongoMetricsReporter.timerReadBayesianExperimentStatistics();
        try {
            Query query = new Query();

            query.addCriteria(Criteria.where("experimentId").is(experimentId));
            query.addCriteria(Criteria.where("device").is(device));

            return (List)(timer.wrap(() -> mongoTemplate.find(query, ENTITY, COLLECTION)).call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(BayesianExperimentStatisticsForVariant newStats) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteBayesianExperimentStatistics();
        timer.record(() -> {
            logger.info("Saving new Bayesian stats for: {} {} {} {}", newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getToDate());
            mongoTemplate.save(newStats, COLLECTION);

            removeOldStats(newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getToDate());
        });
    }

    private void removeOldStats(String expId, DeviceClass device, String variantName, String toDate) {
        Query delete = new Query();

        delete.addCriteria(Criteria.where("experimentId").is(expId));
        delete.addCriteria(Criteria.where("device").is(device));
        delete.addCriteria(Criteria.where("variantName").is(variantName));
        delete.addCriteria(Criteria.where("toDate").lt(toDate));

        DeleteResult remove = mongoTemplate.remove(delete, ENTITY, COLLECTION);
        logger.info("Removed {} old Bayesian stats for: {} {} {}", remove.getDeletedCount(), expId, device, variantName);
    }
}
