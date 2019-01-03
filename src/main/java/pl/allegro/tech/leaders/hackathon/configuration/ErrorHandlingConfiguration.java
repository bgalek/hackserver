package pl.allegro.tech.leaders.hackathon.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;
import org.zalando.problem.Status;
import org.zalando.problem.validation.ConstraintViolationProblemModule;

@RestControllerAdvice
class ErrorHandlingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingConfiguration.class);

    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule();
    }

    @Bean
    public ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Problem> responseStatusException(ResponseStatusException responseStatusException) {
        return ResponseEntity.status(responseStatusException.getStatus())
                .body(Problem.builder()
                        .withStatus(Status.valueOf(responseStatusException.getStatus().name()))
                        .withDetail(responseStatusException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Problem> unhandledException(Exception exception) {
        logger.error("Unhandled exception ☠️☠️☠️", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Problem.builder()
                        .withDetail(exception.getClass().getName())
                        .withStatus(Status.INTERNAL_SERVER_ERROR)
                        .build()
                );
    }
}
