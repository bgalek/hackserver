package pl.allegro.experiments.chi.chiserver.application.statistics

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.experiments.chi.chiserver.domain.statistics.StatisticsRepository
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint

@RestController
@RequestMapping(value = ["/api"], produces = [APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE])
class StatisticsController(private val experimentsRepository: ExperimentsRepository,
                           private val statisticsRepository: StatisticsRepository,
                           private val jsonConverter: JsonConverter) {

    private val logger = LoggerFactory.getLogger(StatisticsController::class.java)

    @MeteredEndpoint
    @GetMapping("/statistics/{experimentId}")
    fun experimentStatistics(@PathVariable experimentId: String,
                             @RequestParam(value = "device", required = false) device: String?)
            : ResponseEntity<String> {
        logger.debug("Experiment statistics request received")

        val experiment = experimentsRepository.getExperiment(experimentId)
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val lastDate = statisticsRepository.lastStatisticsDate(experiment)
                ?: return ResponseEntity(HttpStatus.NO_CONTENT)


        val device = device ?: "all"

        return statisticsRepository.experimentStatistics(experiment, lastDate, device)
                .let { jsonConverter.toJson(it) }
                .let { ResponseEntity.ok(it) }
    }
}