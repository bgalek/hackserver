package pl.allegro.experiments.chi.chiserver.experiments.infrastructure;

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.slf4j.LoggerFactory
import java.io.IOException

object ClasspathContentLoader {

    private val logger = LoggerFactory.getLogger(ClasspathContentLoader::class.java)

    fun localResource(jsonPath: String): String {
        logger.debug("loading data from classpath resource: $jsonPath ...")

        try {
            val url = Resources.getResource(jsonPath)
            return Resources.toString(url, Charsets.UTF_8)
        } catch (e: IOException) {
            val message = "IOException: " + e.message + " while getting resource: " + jsonPath
            logger.error(message)
            throw RuntimeException(message, e)
        }
    }
}
