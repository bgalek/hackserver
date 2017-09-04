package pl.allegro.experiments.chi.chiserver.api

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import pl.allegro.tech.common.andamio.endpoint.PublicEndpoint
import pl.allegro.experiments.chi.chiserver.domain.DomainConfiguration
import pl.allegro.experiments.chi.chiserver.domain.DomainObject
import pl.allegro.experiments.chi.chiserver.logger
import javax.validation.Valid

@RestController
@RequestMapping(value = "/service/resources", produces = arrayOf(APPLICATION_JSON_VALUE))
class ExampleEndpoint constructor(val config: DomainConfiguration) {

    companion object {
        private val logger by logger()
    }

    @PublicEndpoint
    @GetMapping("public")
    @HystrixCommand
    fun publicResource() : DomainObject {
        logger.info("Public resource request received")

        return DomainObject(value = "public resource")
    }

    @GetMapping("local")
    @HystrixCommand(groupKey = "service", commandKey = "local")
    fun localResource() : DomainObject {
        logger.info("Local resource request received")

        return DomainObject(value = "local resource")
    }

    @PostMapping("pong", consumes = arrayOf(APPLICATION_JSON_VALUE))
    @HystrixCommand(groupKey = "service", commandKey = "local")
    fun pongResource(@RequestBody @Valid input : DomainObject) : DomainObject {
        logger.info("Pong resource request received")

        return input.copy(
                value = "${input.value} ${config.value ?: "empty pong"}",
                ttl = (input.ttl ?: 255) - 1
        )
    }
}
