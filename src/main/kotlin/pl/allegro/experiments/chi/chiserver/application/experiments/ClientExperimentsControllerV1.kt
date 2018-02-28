package pl.allegro.experiments.chi.chiserver.application.experiments

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.CrisisManagementProperties
import pl.allegro.experiments.chi.chiserver.logger
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(
    value = ["/api/experiments"],
    produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE]
)
class ClientExperimentsControllerV1(
    private val experimentsRepository: ExperimentsRepository,
    private val jsonConverterV1: JsonConverter,
    private val crisisManagementFilter: CrisisManagementFilter
) {

    companion object {
        private val logger by logger()
    }

    @MeteredEndpoint
    @GetMapping(path = ["/v1"])
    fun activeExperiments() : String {
        logger.info("Active experiments request received")
        return jsonConverterV1.toJson(experimentsRepository.getAll()
                .filter {crisisManagementFilter.filter(it)}
                .filter {it.status != ExperimentStatus.ENDED && it.status != ExperimentStatus.PAUSED})
    }
}