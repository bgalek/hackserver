package pl.allegro.experiments.chi.chiserver.assignments

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.allegro.experiments.chi.chiserver.logger

@RestController
@RequestMapping(value = "/api/assignments", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE))
class AssignmentsController(private val eventEmitter: EventEmitter) {

    companion object {
        private val logger by logger()
    }

    @PostMapping
    fun assignToExperiments(@RequestBody experimentAssignments: ExperimentAssignmentsDto): Unit {
        AssignmentsController.logger.info("analytics")
        experimentAssignments.experimentAssignmentDtos
                .forEach { experimentAssignment -> eventEmitter.emit(experimentAssignment.toEvent()) }
    }
}