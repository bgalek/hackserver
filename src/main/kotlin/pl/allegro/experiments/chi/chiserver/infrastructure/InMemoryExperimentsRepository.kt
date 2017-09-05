package pl.allegro.experiments.chi.chiserver.infrastructure

import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository


class InMemoryExperimentsRepository: ExperimentsRepository {
    override fun getAllExperiments(): List<Experiment> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
