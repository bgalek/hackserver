package pl.allegro.experiments.chi.chiserver.infrastructure.statistics

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.statistics.BayesianStatisticsRepository
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.Samples
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.VariantBayesianStatistics

class BayesianExperimentStatisticsIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    BayesianStatisticsRepository bayesianStatisticsRepository

    @Autowired
    MongoTemplate mongoTemplate

    def "should save and return bayesian statistics"() {
        given:
        def bayesianStats = [
                new VariantBayesianStatistics ('base', new Samples([0.1, 0.2, 0.3], [1, 2, 3])),
                new VariantBayesianStatistics ('variant1', new Samples([0.2, 0.2, 0.2], [1, 1, 1]))
        ]
        def stats = [
            experimentId: 'experiment1',
            toDate: '2018-04-01',
            device: 'all',
            variantBayesianStatistics: bayesianStats
        ]

        mongoTemplate.save(stats, MongoBayesianStatisticsRepository.COLLECTION)

        when:
        def result = bayesianStatisticsRepository.experimentStatistics('experiment1', '2018-04-01', 'all').get()

        then:
        result.device == 'all'
        result.experimentId == 'experiment1'
        result.toDate == '2018-04-01'
        result.variantBayesianStatistics[0].samples.values == bayesianStats[0].samples.values
        result.variantBayesianStatistics[0].samples.counts == bayesianStats[0].samples.counts
        result.variantBayesianStatistics[1].samples.values == bayesianStats[1].samples.values
        result.variantBayesianStatistics[1].samples.counts == bayesianStats[1].samples.counts
    }
}
