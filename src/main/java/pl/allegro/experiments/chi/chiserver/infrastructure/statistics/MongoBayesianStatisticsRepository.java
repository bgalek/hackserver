package pl.allegro.experiments.chi.chiserver.infrastructure.statistics;

import io.micrometer.core.instrument.Timer;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsMongoMetricsReporter;

import java.util.Optional;

public class MongoBayesianStatisticsRepository implements BayesianStatisticsRepository {

    private static final String COLLECTION = "bayesianExperimentStatistics";
    private final MongoTemplate mongoTemplate;
    private final ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter;

    public MongoBayesianStatisticsRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter) {
        this.mongoTemplate = mongoTemplate;
        this.experimentsMongoMetricsReporter = experimentsMongoMetricsReporter;
    }

    @Override
    public Optional<BayesianExperimentStatistics> experimentStatistics(String experimentId, String toDate, String device) {
        Timer timer = experimentsMongoMetricsReporter.timerReadBayesianExperimentStatistics();
        Query query = new Query();

        query.addCriteria(Criteria.where("experimentId").is(experimentId));
        query.addCriteria(Criteria.where("toDate").is(toDate));
        query.addCriteria(Criteria.where("device").is(device));

        BayesianExperimentStatistics result = mongoTemplate.findOne(query, BayesianExperimentStatistics.class, COLLECTION);
        timer.close();
        return Optional.ofNullable(result);
    }

    @Override
    public void save(BayesianExperimentStatistics experimentStatistics) {
        Timer timer = experimentsMongoMetricsReporter.timerWriteBayesianExperimentStatistics();
        mongoTemplate.save(experimentStatistics, COLLECTION);
        timer.close();
    }
}
