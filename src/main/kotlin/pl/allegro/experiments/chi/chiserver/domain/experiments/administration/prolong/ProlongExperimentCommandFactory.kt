package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.prolong

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

@Component
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