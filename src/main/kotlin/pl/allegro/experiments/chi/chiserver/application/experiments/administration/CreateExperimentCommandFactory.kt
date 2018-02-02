package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentCreationRequest
import pl.allegro.experiments.chi.chiserver.logger

@Component
class CreateExperimentCommandFactory(val experimentsRepository: ExperimentsRepository,
                                     val userProvider: UserProvider) {
    companion object {
        private val logger by logger()
    }

    fun createExperimentCommand(request: ExperimentCreationRequest) : CreateExperimentCommand {
        return CreateExperimentCommand(experimentsRepository, userProvider, request)
    }
}
