package pl.allegro.experiments.chi.chiserver.api

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.experiments.chi.persistence.FileBasedExperimentsRepository
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint


@RestController
@RequestMapping(value = "/api/experiments", produces = arrayOf(APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE))
class ExperimentsController {

    companion object {
        private val logger by logger()
    }

    @Autowired
    lateinit var experimentsRepository: FileBasedExperimentsRepository

    @PublicEndpoint
    @MeteredEndpoint
    @GetMapping("")
    fun activeExperiments() : String {
        ExperimentsController.logger.info("Active experiments request received")

        return experimentsRepository.allAsJSON
    }

}
