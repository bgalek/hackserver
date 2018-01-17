package pl.allegro.experiments.chi.chiserver.infrastructure.druid

import org.springframework.http.HttpStatus
import pl.allegro.tech.common.andamio.errors.spring.LogLevel
import pl.allegro.tech.common.andamio.errors.spring.MappableToErrorResponse

class DruidException(message: String, cause: Throwable) :
    RuntimeException(message, cause), MappableToErrorResponse {

    override fun getHttpResponseStatus() = HttpStatus.SERVICE_UNAVAILABLE

    override fun getErrorCode() = "DRUID_ERROR"

    override fun getLogLevel() = LogLevel.WARN

    override fun getErrorMessage() = message
}