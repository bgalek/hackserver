package pl.allegro.experiments.chi.chiserver.infrastructure

import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.logger

class FileBasedExperimentsRepository(var fileUrl: String, val restTemplate: RestTemplate) : ExperimentsRepository {
    companion object {
        private val logger by logger()
    }

    internal val jsonConverter = JsonConverter()

    override fun getAllExperiments(): List<Experiment> {
        val content = loadFromHttp(fileUrl)
        return jsonConverter.fromJSON(content);
    }

    private fun loadFromHttp(jsonUrl: String): String {
        logger.debug("loading data from URL: $jsonUrl ...")
        val headers = HttpHeaders()
        val entity = HttpEntity<String>(headers)
        return restTemplate.exchange(jsonUrl, HttpMethod.GET, entity, String::class.java).body
    }
}
