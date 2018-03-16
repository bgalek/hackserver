package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.javers.core.Javers
import org.springframework.data.mongodb.core.MongoTemplate
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

private const val COLLECTION = "experiments"

open class MongoExperimentsRepository(val mongoTemplate: MongoTemplate,
                                      val experimentsMongoMetricsReporter: ExperimentsMongoMetricsReporter,
                                      val javers: Javers,
                                      val userProvider: UserProvider) :
        ExperimentsRepository {
    override fun getExperiment(id: String): Experiment? {
        experimentsMongoMetricsReporter.timerSingleExperiment().use {
            return mongoTemplate.findById(id, Experiment::class.java, COLLECTION)
        }
    }

    override fun delete(experimentId: String) {
        val experiment = getExperiment(experimentId)
        val userName = userProvider.currentUser.name
        javers.getLatestSnapshot(experimentId, Experiment::class.java).ifPresent {
            javers.commitShallowDelete(userName, experiment)
        }
        mongoTemplate.remove(experiment, COLLECTION)
    }

    override fun save(experiment: Experiment) {
        val userName = userProvider.currentUser.name
        javers.commit(userName, experiment)
        mongoTemplate.save(experiment, COLLECTION)
    }

    override fun getAll(): List<Experiment> {
        experimentsMongoMetricsReporter.timerAllExperiments().use {
            return mongoTemplate.findAll(Experiment::class.java, COLLECTION)
        }
    }

    override fun assignable(): List<Experiment> {
        return getAll().filter { it.isAssignable() }
    }
}