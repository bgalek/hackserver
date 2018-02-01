package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

private const val COLLECTION = "experiments"

open class MongoExperimentsRepository(private val mongoTemplate: MongoTemplate, val experimentsMongoMetricsReporter: ExperimentsMongoMetricsReporter) : ExperimentsRepository {
    override fun getExperiment(id: ExperimentId): Experiment? {
        experimentsMongoMetricsReporter.timerSingleExperiment().use {
            return mongoTemplate.findById(id, Experiment::class.java, COLLECTION)
        }
    }

    override fun save(experiment: Experiment) =
        mongoTemplate.save(experiment, COLLECTION)

    override val all: List<Experiment>
        get() {
            experimentsMongoMetricsReporter.timerAllExperiments().use {
                return mongoTemplate.findAll(Experiment::class.java, COLLECTION)
            }
        }

    override val overridable: List<Experiment>
        get() = all.filter { it.isOverridable() }

    override fun refresh() {
    }
}