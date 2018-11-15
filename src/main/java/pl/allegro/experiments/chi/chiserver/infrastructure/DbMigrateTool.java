package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant;

import javax.annotation.PostConstruct;

@Service
public class DbMigrateTool {
    private static final Logger logger = LoggerFactory.getLogger(DbMigrateTool.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DbMigrateTool(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @PostConstruct
    public void action() throws Exception {
        logger.info("running DbMigrateTool ...");

        updateBayesianStats();
    }

    private void updateBayesianStats() {
        Query query = new Query().addCriteria(Criteria.where("metricName").is(null));
        var oldStats = mongoTemplate.count(query, "bayesianExperimentStatistics");

        logger.info("oldStats: {}", oldStats);

        if (oldStats > 0) {
            logger.info("migrating {} BayesianExperimentStatisticsForVariant objects", oldStats);
            var result = mongoTemplate.updateMulti(query, new Update().set("metricName", "tx_visit"), "bayesianExperimentStatistics");
            logger.info("updated documents: {}", result.getModifiedCount());
        }
    }

}