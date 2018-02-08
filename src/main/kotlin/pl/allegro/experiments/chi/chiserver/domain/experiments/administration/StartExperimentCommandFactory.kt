package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentId
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

@Component
class StartExperimentCommandFactory(val experimentsRepository: ExperimentsRepository,
                                    val userProvider: UserProvider) {

    fun startExperimentCommand(
            experimentId: ExperimentId,
            properties: StartExperimentProperties) : StartExperimentCommand {
        return StartExperimentCommand(experimentsRepository, userProvider, properties, experimentId)
    }
}
