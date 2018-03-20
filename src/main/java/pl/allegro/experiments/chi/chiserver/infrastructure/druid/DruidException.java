package pl.allegro.experiments.chi.chiserver.infrastructure.druid;

import org.springframework.http.HttpStatus;
import pl.allegro.tech.common.andamio.errors.spring.LogLevel;
import pl.allegro.tech.common.andamio.errors.spring.MappableToErrorResponse;

public class DruidException extends RuntimeException implements MappableToErrorResponse {
    DruidException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpResponseStatus() {
        return HttpStatus.SERVICE_UNAVAILABLE;
    }

    @Override
    public String getErrorCode() {
        return "DRUID_ERROR";
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.WARN;
    }

    @Override
    public String getErrorMessage() {
        return getMessage();
    }
}
