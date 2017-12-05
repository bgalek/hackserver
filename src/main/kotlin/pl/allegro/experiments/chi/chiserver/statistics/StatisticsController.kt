package pl.allegro.experiments.chi.chiserver.statistics

import org.slf4j.LoggerFactory
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.JsonConverter
import pl.allegro.tech.common.andamio.metrics.MeteredEndpoint
import java.time.LocalDate
import java.time.ZonedDateTime

@RestController
@RequestMapping(value = "/api", produces = arrayOf(APPLICATION_JSON_VALUE, APPLICATION_JSON_UTF8_VALUE))
class StatisticsController(private val experimentsRepository: ExperimentsRepository,
                           private val statisticsRepository: StatisticsRepository,
                           private val jsonConverter: JsonConverter) {

    private val logger = LoggerFactory.getLogger(StatisticsController::class.java)

    @MeteredEndpoint
    @GetMapping("/statistics/{experimentId}")
    fun experimentStatistics(@PathVariable experimentId: String,
                             @RequestParam(value = "toDate", required = false)
                             @DateTimeFormat(pattern="yyyy-MM-dd") toDate: LocalDate?,
                             @RequestParam(value = "device", required = false) device: String?)
            : ResponseEntity<String> {
        val toDate = toDate ?: ZonedDateTime.now().minusDays(1).toLocalDate()
        val device = device ?: "all"

        logger.info("Experiment statistics request received")
        return experimentsRepository.getExperiment(experimentId)
                ?.let { statisticsRepository.experimentStatistics(it.id, toDate, device) }
                ?.let { jsonConverter.toJson(it) }
                ?.let { ResponseEntity.ok(it) }
                ?:(ResponseEntity(HttpStatus.NOT_FOUND))
    }
}