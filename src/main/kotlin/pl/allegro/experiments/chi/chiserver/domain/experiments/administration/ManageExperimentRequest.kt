package pl.allegro.experiments.chi.chiserver.domain.experiments.administration

enum class ExperimentCommand {
    START, DELETE
}

class ManageExperimentRequest<T>(val command: ExperimentCommand,
                                 val commandProperties: T)