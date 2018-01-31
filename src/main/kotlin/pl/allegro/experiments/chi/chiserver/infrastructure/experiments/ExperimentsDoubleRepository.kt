package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.slf4j.LoggerFactory
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

class ExperimentsDoubleRepository(private val fileBasedExperimentsRepository: FileBasedExperimentsRepository,
                                  private val mongoExperimentsRepository: MongoExperimentsRepository) : ExperimentsRepository {

    companion object {
        private val logger = LoggerFactory.getLogger(ExperimentsDoubleRepository::class.java)
    }

    init {
        refresh()
    }

    override fun getExperiment(id: ExperimentId): Experiment? {
        return fileBasedExperimentsRepository.getExperiment(id) ?: mongoExperimentsRepository.getExperiment(id)
    }

    override fun save(experiment: Experiment) {
        mongoExperimentsRepository.save(experiment)
    }

    override val all: List<Experiment>
    get() = fileBasedExperimentsRepository.all + mongoExperimentsRepository.all


    override val assignable: List<Experiment>
    get() = all.filter { it.isAssignable() }

    private fun refresh() {
        fileBasedExperimentsRepository.refresh()
    }
}