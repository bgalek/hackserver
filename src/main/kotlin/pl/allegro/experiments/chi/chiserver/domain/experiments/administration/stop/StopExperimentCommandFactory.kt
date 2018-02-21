package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

@Component
class StopExperimentCommandFactory(
        val experimentsRepository: ExperimentsRepository,
        val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter) {
    fun stopExperimentCommand(experimentId: String): StopExperimentCommand {
        return StopExperimentCommand(experimentId, experimentsRepository, permissionsAwareExperimentGetter)
    }
}