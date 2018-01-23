package pl.allegro.experiments.chi.chiserver.application.experiments.administration

import org.springframework.stereotype.Component
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository

@Component
class CreateExperimentCommand(val experimentsRepository: ExperimentsRepository) {

    fun createExperiment()  {

        val e = Experiment(
                id= "a",
                description = "desc",
                groups = emptyList(),
                variants = emptyList(),
                author = "x"
        )

        experimentsRepository.save(e)
    }

}
