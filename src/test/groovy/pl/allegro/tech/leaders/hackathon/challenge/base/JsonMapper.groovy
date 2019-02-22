package pl.allegro.tech.leaders.hackathon.challenge.base

import com.fasterxml.jackson.databind.ObjectMapper
import pl.allegro.tech.leaders.hackathon.configuration.ObjectMapperProvider

class JsonMapper {
    static ObjectMapper objectMapper = ObjectMapperProvider.objectMapper()

    static String toJson(Object obj) {
        return objectMapper
            .writerWithDefaultPrettyPrinter()
            .writeValueAsString(obj)
    }

    static <T> T parseJson(String json, Class<T> expectedType) {
        return objectMapper.readValue(json, expectedType)
    }
}
