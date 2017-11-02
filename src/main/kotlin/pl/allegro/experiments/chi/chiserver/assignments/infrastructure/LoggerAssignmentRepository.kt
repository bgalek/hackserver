package pl.allegro.experiments.chi.chiserver.assignments.infrastructure

import pl.allegro.experiments.chi.chiserver.assignments.Assignment
import pl.allegro.experiments.chi.chiserver.assignments.AssignmentRepository
import java.util.logging.Logger

class LoggerAssignmentRepository: AssignmentRepository {

    companion object {
        private val logger = Logger.getLogger(LoggerAssignmentRepository::class.java.name)
    }

    override fun save(assignment: Assignment) {
        logger.info("""
             userId: ${assignment.userId}
             userCmId: ${assignment.userCmId}
             experimentId: ${assignment.experimentId}
             variantName: ${assignment.variantName}
             internal: ${assignment.internal}
             confirmed: ${assignment.confirmed}
             deviceClass: ${assignment.deviceClass}
             assignmentDate: ${assignment.assignmentDate}
        """.trimIndent())
    }
}