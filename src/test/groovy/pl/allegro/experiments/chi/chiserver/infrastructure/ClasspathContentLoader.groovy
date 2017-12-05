package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.common.base.Charsets
import com.google.common.io.Resources
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ClasspathContentLoader {

    private static final Logger logger = LoggerFactory.getLogger(ClasspathContentLoader.class);

    static String localResource(String jsonPath) {
        logger.debug("loading data from classpath resource: " + jsonPath + " ...");

        try {
            URL url = Resources.getResource(jsonPath);
            return Resources.toString(url, Charsets.UTF_8);
        }
        catch (IOException e) {
            String message = "IOException: " + e.getMessage() + " while getting resource: " + jsonPath;
            logger.error(message);
            throw new RuntimeException(message, e);
        }
    }
}
