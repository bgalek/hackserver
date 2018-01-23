package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

private const val COLLECTION = "experiments"

open class MongoExperimentsRepository(private val mongoTemplate: MongoTemplate) : ExperimentsRepository {
    override fun getExperiment(id: ExperimentId): Experiment? =
        mongoTemplate.findById(id, Experiment::class.java, COLLECTION)

    override fun save(experiment: Experiment) =
        mongoTemplate.save(experiment, COLLECTION)

    override val all: List<Experiment>
        get() = mongoTemplate.findAll(Experiment::class.java, COLLECTION)

    override val assignable: List<Experiment>
        // TODO: use mongo query to filter active experiments
        get() = all.filter { it.isActive() }

    override fun refresh() {
    }
}