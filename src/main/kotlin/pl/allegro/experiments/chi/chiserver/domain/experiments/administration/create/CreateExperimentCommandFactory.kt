package pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import pl.allegro.experiments.chi.chiserver.logger

@Component
class CreateExperimentCommandFactory(val experimentsRepository: ExperimentsRepository,
                                     val userProvider: UserProvider) {

    fun createExperimentCommand(request: ExperimentCreationRequest) : CreateExperimentCommand {
        return CreateExperimentCommand(experimentsRepository, userProvider, request)
    }
}