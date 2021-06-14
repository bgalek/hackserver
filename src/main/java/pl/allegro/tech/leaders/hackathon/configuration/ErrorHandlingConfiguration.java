package pl.allegro.tech.leaders.hackathon.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.violations.ConstraintViolationProblemModule;

@RestControllerAdvice
class ErrorHandlingConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(ErrorHandlingConfiguration.class);

    @Bean
    ConstraintViolationProblemModule constraintViolationProblemModule() {
        return new ConstraintViolationProblemModule();
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Problem> responseStatusException(ResponseStatusException responseStatusException) {
        return ResponseEntity.status(responseStatusException.getStatus())
                .body(Problem.builder()
                        .withStatus(Status.valueOf(responseStatusException.getStatus().name()))
                        .withDetail(responseStatusException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(DuplicateKeyException.class)
    ResponseEntity<Problem> responseStatusException(DuplicateKeyException responseStatusException) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Problem.builder()
                        .withStatus(Status.valueOf(HttpStatus.CONFLICT.name()))
                        .withDetail(responseStatusException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Problem> responseStatusException(HttpMessageNotReadableException responseStatusException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Problem.builder()
                        .withStatus(Status.valueOf(HttpStatus.BAD_REQUEST.name()))
                        .withDetail(responseStatusException.getMessage())
                        .build()
                );
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<Problem> unhandledException(Exception exception) {
        logger.error("Unhandled exception ☠️☠️☠️", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Problem.builder()
                        .withDetail(exception.getClass().getName())
                        .withStatus(Status.INTERNAL_SERVER_ERROR)
                        .build()
                );
    }
}
