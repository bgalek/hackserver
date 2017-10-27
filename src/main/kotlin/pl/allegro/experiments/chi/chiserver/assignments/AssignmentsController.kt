package pl.allegro.experiments.chi.chiserver.assignments

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pl.allegro.experiments.chi.chiserver.logger

@RestController
@RequestMapping(value = "/api/assignments", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE))
class AssignmentsController(private val eventEmitter: ExperimentAssignmentRepository) {

    companion object {
        private val logger by logger()
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    fun assignToExperiments(@RequestBody experimentAssignments: ExperimentAssignmentsDto) {
        AssignmentsController.logger.info("analytics")
        experimentAssignments.experimentAssignmentDtos
                .forEach { experimentAssignment -> eventEmitter.save(experimentAssignment.toEvent()) }
    }
}