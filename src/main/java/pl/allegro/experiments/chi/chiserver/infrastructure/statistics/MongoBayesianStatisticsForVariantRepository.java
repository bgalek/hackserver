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
    public List<BayesianExperimentStatisticsForVariant> getLatestForAllVariants(String experimentId) {
        Timer timer = experimentsMongoMetricsReporter.timerReadBayesianExperimentStatistics();
        try {
            Query query = new Query();

            query.addCriteria(Criteria.where("experimentId").is(experimentId));

            return (List)(timer.wrap(() -> mongoTemplate.find(query, ENTITY, COLLECTION)).call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(BayesianExperimentStatisticsForVariant newStats) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteBayesianExperimentStatistics();
        timer.record(() -> {
            int existingVersion = getExistingVersion(newStats);
            logger.info("Existing version of Bayesian stats for: {} {} {} is {}", newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), existingVersion);

            newStats.setVersion(existingVersion + 1);
            logger.info("Saving new Bayesian stats for: {} {} {} {} {}", newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getToDate(), newStats.getVersion());
            mongoTemplate.save(newStats, COLLECTION);

            removeOldStats(newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getVersion());
        });
    }

    private int getExistingVersion(BayesianExperimentStatisticsForVariant stats) {
        Query select = new Query();
        select.addCriteria(Criteria.where("experimentId").is(stats.getExperimentId()));
        select.addCriteria(Criteria.where("device").is(stats.getDevice()));
        select.addCriteria(Criteria.where("variantName").is(stats.getVariantName()));

        BayesianExperimentStatisticsForVariant existing = (BayesianExperimentStatisticsForVariant)mongoTemplate.findOne(select, ENTITY, COLLECTION);

        if (existing == null) {
            return 0;
        }
        return existing.getVersion();
    }

    private void removeOldStats(String expId, DeviceClass device, String variantName, int currentVersion) {
        Query delete = new Query();

        delete.addCriteria(Criteria.where("experimentId").is(expId));
        delete.addCriteria(Criteria.where("device").is(device));
        delete.addCriteria(Criteria.where("variantName").is(variantName));
        delete.addCriteria(Criteria.where("version").lt(currentVersion));

        DeleteResult remove = mongoTemplate.remove(delete, ENTITY, COLLECTION);
        logger.info("Removed {} old Bayesian stats for: {} {} {}", remove.getDeletedCount(), expId, device, variantName);
    }
}
