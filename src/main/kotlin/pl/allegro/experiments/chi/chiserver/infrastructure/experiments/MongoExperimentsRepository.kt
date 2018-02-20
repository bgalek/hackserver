package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import java.util.*

private const val COLLECTION = "experiments"

open class MongoExperimentsRepository(private val mongoTemplate: MongoTemplate, val experimentsMongoMetricsReporter: ExperimentsMongoMetricsReporter) :
        ExperimentsRepository {
    override fun getExperiment(id: ExperimentId): Experiment? {
        experimentsMongoMetricsReporter.timerSingleExperiment().use {
            return mongoTemplate.findById(id, Experiment::class.java, COLLECTION)
        }
    }

    override fun delete(experimentId: ExperimentId) {
        mongoTemplate.remove(getExperiment(experimentId), COLLECTION)
    }

    override fun save(experiment: Experiment) =
        mongoTemplate.save(experiment, COLLECTION)

    override fun getAll() : List<Experiment> {
        experimentsMongoMetricsReporter.timerAllExperiments().use {
            return mongoTemplate.findAll(Experiment::class.java, COLLECTION)
        }
    }

    override fun overridable(): List<Experiment> {
        return getAll().filter { it.isOverridable() }
    }
}