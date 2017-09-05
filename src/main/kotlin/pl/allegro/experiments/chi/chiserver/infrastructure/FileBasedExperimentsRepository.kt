package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.base.Charsets
import com.google.common.io.Files
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.web.client.RestTemplate
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.logger
import java.io.File
import java.io.IOException

class FileBasedExperimentsRepository(val jsonUrl: String, val restTemplate: RestTemplate) : ExperimentsRepository {
    companion object {
        private val logger by logger()
    }

    internal val jsonConverter = JsonConverter()
    internal val dataLoader: () -> String

    init {
        if (jsonUrl.toLowerCase().startsWith("http")) {
            dataLoader = { loadFromHttp(jsonUrl) }
        } else {
            dataLoader = { loadFromFile(jsonUrl) }
        }
    }

    override fun getAllExperiments(): List<Experiment> {
        val content = dataLoader.invoke()
        return jsonConverter.fromJSON(content);
    }

    private fun loadFromFile(jsonUrl: String): String {
        FileBasedExperimentsRepository.logger.debug("loading data from file: $jsonUrl ...")

        try {
            return Files.asCharSource(File(jsonUrl), Charsets.UTF_8).read()
        } catch (e: IOException) {
            val message = "IOException: " + e.message + " while getting resource: " + jsonUrl
            logger.error(message)
            throw RuntimeException(message, e)
        }
    }

    private fun loadFromHttp(jsonUrl: String): String {
        logger.debug("loading data from URL: $jsonUrl ...")
        val headers = HttpHeaders()
        val entity = HttpEntity<String>(headers)
        return restTemplate.exchange(jsonUrl, HttpMethod.GET, entity, String::class.java).body
    }
}
