package pl.allegro.experiments.chi.chiserver.experiments.infrastructure

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.logger

class HttpContentLoader(private val restTemplate: RestTemplate) {
    companion object {
        private val logger by logger()
    }

    fun loadFromHttp(jsonUrl: String): String {
        logger.info("loading data from URL: $jsonUrl ...")
        val headers = HttpHeaders()
        val entity = HttpEntity<String>(headers)
        return restTemplate.exchange(jsonUrl, HttpMethod.GET, entity, String::class.java).body
    }
}
