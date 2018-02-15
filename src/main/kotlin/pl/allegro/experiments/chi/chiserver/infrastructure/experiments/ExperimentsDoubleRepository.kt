package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import pl.allegro.experiments.chi.chiserver.domain.experiments.*

class ExperimentsDoubleRepository(private val readOnlyExperimentsRepository: ReadOnlyExperimentsRepository,
                                  private val mongoExperimentsRepository: ExperimentsRepository) : ExperimentsRepository {

    override fun getExperiment(id: ExperimentId): Experiment? {

        return readOnlyExperimentsRepository.getExperiment(id)
               ?: mongoExperimentsRepository.getExperiment(id)
    }

    override fun save(experiment: Experiment) {
        mongoExperimentsRepository.save(experiment)
    }

    override fun delete(experimentId: ExperimentId) {
        mongoExperimentsRepository.delete(experimentId)
    }

    override fun getAll() : List<Experiment> {
        val readOnlyExperiments = readOnlyExperimentsRepository.getAll()
        val readOnlyKeys = HashSet<String>(readOnlyExperiments.map(Experiment::id))

        val merged = ArrayList(readOnlyExperiments)

        mongoExperimentsRepository.getAll().forEach { e ->
            if (!readOnlyKeys.contains(e.id)) {
                merged.add(e)
            }
        }

        return merged
    }

    fun getOrigin(experiment: Experiment) : String {
        if (readOnlyExperimentsRepository.getExperiment(experiment.id) != null) {
            return "stash"
        }
        if (mongoExperimentsRepository.getExperiment(experiment.id) != null) {
            return "mongo"
        }
        return "not found :("
    }

    override val overridable: List<Experiment>
    get() = getAll().filter { it.isOverridable() }
}