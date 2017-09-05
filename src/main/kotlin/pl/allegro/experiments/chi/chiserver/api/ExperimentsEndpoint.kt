package pl.allegro.experiments.chi.chiserver.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint


@RestController
@RequestMapping(value = "/api/experiments", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
class ExperimentsEndpoint {

    companion object {
        private val logger by logger()
    }

    @Autowired
    lateinit var experimentsRepository: InMemoryExperimentsRepository

    @PublicEndpoint
    @GetMapping("")
    fun activeExperiments() : List<Experiment> {
        ExperimentsEndpoint.logger.info("Active experiments request received")

        return experimentsRepository.getAllExperiments()
    }

}
