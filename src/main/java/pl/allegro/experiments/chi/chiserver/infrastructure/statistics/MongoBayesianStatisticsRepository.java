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
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

import java.util.Optional;

@Repository
public class MongoBayesianStatisticsRepository implements BayesianStatisticsRepository {
    private static final Logger logger = LoggerFactory.getLogger(MongoBayesianStatisticsRepository.class);

    private static final String COLLECTION = "bayesianExperimentStatistics";
    private final MongoTemplate mongoTemplate;
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;

    @Autowired
    public MongoBayesianStatisticsRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        this.mongoTemplate = mongoTemplate;
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
    }

    @Override
    public Optional<BayesianExperimentStatistics> experimentStatistics(String experimentId, String device, String toDate) {
        Timer timer = experimentsMongoMetricsReporter.timerReadBayesianExperimentStatistics();
        try {
            return Optional.ofNullable(timer.wrap(() -> mongoTemplate.findOne(query(experimentId, device, toDate), BayesianExperimentStatistics.class, COLLECTION)).call());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(BayesianExperimentStatistics experimentStatistics) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteBayesianExperimentStatistics();
        timer.record(() -> {
            BayesianExperimentStatistics merged = this.experimentStatistics(experimentStatistics.getExperimentId(), experimentStatistics.getDevice(), experimentStatistics.getToDate())
                    .map(old -> old.updateVariants(experimentStatistics))
                    .orElse(experimentStatistics);
            // TODO: replace with upsert 
            removeExistingStats(merged.getExperimentId(), merged.getDevice(), merged.getToDate());
            mongoTemplate.save(merged, COLLECTION);
        });
    }

    private void removeExistingStats(String experimentId, String device, String toDate) {
        DeleteResult remove = mongoTemplate.remove(query(experimentId, device, toDate), BayesianExperimentStatistics.class, COLLECTION);
        logger.info("Removed {} documents", remove.getDeletedCount());
    }

    private Query query(String experimentId, String device, String toDate) {
        Query query = new Query();

        query.addCriteria(Criteria.where("experimentId").is(experimentId));
        query.addCriteria(Criteria.where("toDate").is(toDate));
        query.addCriteria(Criteria.where("device").is(device));

        return query;
    }
}
