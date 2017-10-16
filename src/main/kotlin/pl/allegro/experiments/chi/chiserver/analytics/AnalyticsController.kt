package pl.allegro.experiments.chi.chiserver.analytics

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = "/api/analytics", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE))
class AnalyticsController(private val eventEmitter: EventEmitter) {

    @PostMapping
    fun assignToExperiments(@RequestBody experimentAssignments: List<ExperimentAssignment>): Unit {
        experimentAssignments.forEach { experimentAssignment -> eventEmitter.emit(experimentAssignment) }
    }
}