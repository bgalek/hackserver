package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.*

class ExperimentsDoubleRepository(private val readOnlyExperimentsRepository: ReadOnlyExperimentsRepository,
                                  private val mongoExperimentsRepository: ExperimentsRepository) : ExperimentsRepository {

    override fun getExperiment(id: String): Experiment? {

        return readOnlyExperimentsRepository.getExperiment(id)
               ?: mongoExperimentsRepository.getExperiment(id)
    }

    override fun save(experiment: Experiment) {
        if (getOrigin(experiment.id) == "stash") {
            throw IllegalArgumentException("experiment $experiment.id is from stash")
        }
        mongoExperimentsRepository.save(experiment)
    }

    override fun delete(experimentId: String) {
        mongoExperimentsRepository.delete(experimentId)
    }

    override fun getAll() : List<Experiment> {
        val readOnlyExperiments = readOnlyExperimentsRepository.getAll()
        val readOnlyKeys = HashSet<String>(readOnlyExperiments.map(Experiment::getId))

        val merged = ArrayList(readOnlyExperiments)

        mongoExperimentsRepository.getAll().forEach { e ->
            if (!readOnlyKeys.contains(e.id)) {
                merged.add(e)
            }
        }

        return merged
    }

    override fun getOrigin(experimentId: String) : String {
        if (readOnlyExperimentsRepository.getExperiment(experimentId) != null) {
            return "stash"
        }
        if (mongoExperimentsRepository.getExperiment(experimentId) != null) {
            return "mongo"
        }
        return super.getOrigin(experimentId)
    }

    override fun assignable(): List<Experiment> {
        return all.filter { it.isAssignable() }
    }
}