package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong

import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

class ProlongExperimentCommandFactory(
        val experimentsRepository: ExperimentsRepository,
        val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter) {

    fun prolongExperimentCommand(
            experimentId: ExperimentId,
            properties: ProlongExperimentProperties): ProlongExperimentCommand {
        return ProlongExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentGetter,
                experimentId)
    }
}