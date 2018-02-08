package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

@Component
class StartExperimentCommandFactory(val experimentsRepository: ExperimentsRepository,
                                    val permissionsAwareExperimentGetter: PermissionsAwareExperimentGetter) {

    fun startExperimentCommand(
            experimentId: ExperimentId,
            properties: StartExperimentProperties) : StartExperimentCommand {
        return StartExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentGetter,
                experimentId)
    }
}