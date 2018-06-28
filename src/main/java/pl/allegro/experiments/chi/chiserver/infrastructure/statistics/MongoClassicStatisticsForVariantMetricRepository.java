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
import pl.allegro.experiments.chi.chiserver.domain.statistics.clasic.ClassicExperimentStatisticsForVariantMetric;
import pl.allegro.experiments.chi.chiserver.domain.statistics.clasic.ClassicStatisticsForVariantMetricRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

import java.util.List;

@Repository
public class MongoClassicStatisticsForVariantMetricRepository implements ClassicStatisticsForVariantMetricRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoClassicStatisticsForVariantMetricRepository.class);

    private static final String COLLECTION = "classicExperimentStatistics";
    private static final Class<ClassicExperimentStatisticsForVariantMetric> ENTITY = ClassicExperimentStatisticsForVariantMetric.class;

    private final MongoTemplate mongoTemplate;
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;

    @Autowired
    MongoClassicStatisticsForVariantMetricRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        this.mongoTemplate = mongoTemplate;
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
    }

    @Override
    public List<ClassicExperimentStatisticsForVariantMetric> getLatestForAllMetricsAndVariants(String experimentId, DeviceClass device) {
        Timer timer = experimentsMongoMetricsReporter.timerReadClassicExperimentStatistics();
        try {
            Query query = new Query();

            query.addCriteria(Criteria.where("experimentId").is(experimentId));
            query.addCriteria(Criteria.where("device").is(device));

            return (timer.wrap(() -> mongoTemplate.find(query, ENTITY, COLLECTION)).call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(ClassicExperimentStatisticsForVariantMetric newStats) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteClassicExperimentStatistics();
        timer.record(() -> {
            int existingVersion = getExistingVersion(newStats);
            logger.info("Existing version of classic stats for: {} {} {} {} is {}", newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getMetricName(), existingVersion);

            newStats.setVersion(existingVersion + 1);
            logger.info("Saving new classic stats for: {} {} {} {} {} {}", newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getToDate(), newStats.getMetricName(), newStats.getVersion());
            mongoTemplate.save(newStats, COLLECTION);

            removeOldStats(newStats.getExperimentId(), newStats.getDevice(), newStats.getVariantName(), newStats.getMetricName(), newStats.getVersion());
        });
    }

    private int getExistingVersion(ClassicExperimentStatisticsForVariantMetric stats) {
        Query select = new Query();
        select.addCriteria(Criteria.where("experimentId").is(stats.getExperimentId()));
        select.addCriteria(Criteria.where("device").is(stats.getDevice()));
        select.addCriteria(Criteria.where("variantName").is(stats.getVariantName()));
        select.addCriteria(Criteria.where("metricName").is(stats.getMetricName()));

        ClassicExperimentStatisticsForVariantMetric existing = mongoTemplate.findOne(select, ENTITY, COLLECTION);

        if (existing == null) {
            return 0;
        }
        return existing.getVersion();
    }

    private void removeOldStats(String expId, DeviceClass device, String variantName, String metricName, int currentVersion) {
        Query delete = new Query();

        delete.addCriteria(Criteria.where("experimentId").is(expId));
        delete.addCriteria(Criteria.where("device").is(device));
        delete.addCriteria(Criteria.where("variantName").is(variantName));
        delete.addCriteria(Criteria.where("metricName").is(metricName));
        delete.addCriteria(Criteria.where("version").lt(currentVersion));

        DeleteResult remove = mongoTemplate.remove(delete, ENTITY, COLLECTION);
        logger.info("Removed {} old classic stats for: {} {} {} {}", remove.getDeletedCount(), expId, device, variantName, metricName);
    }
}
