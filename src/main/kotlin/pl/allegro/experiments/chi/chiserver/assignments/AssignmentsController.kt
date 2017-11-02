package pl.allegro.experiments.chi.chiserver.assignments

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(value = "/api/assignments", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_JSON_UTF8_VALUE))
class AssignmentsController(
        private val assignmentRepository: AssignmentRepository) {

    @PostMapping(path = arrayOf("/v1", ""))
    @ResponseStatus(value = HttpStatus.ACCEPTED)
    fun assignToExperiments(@RequestBody assignments: AssignmentsDto) {
        assignments.assignmentDtos
                .forEach { experimentAssignment -> assignmentRepository.save(experimentAssignment.toEvent()) }
    }
}