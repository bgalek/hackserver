package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.Duration;

class DurationTypeAdapter implements JsonSerializer<Duration>, JsonDeserializer<Duration> {
    @Override
    public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toMillis());
    }

    @Override
    public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Duration.ofMillis(json.getAsLong());
    }
}
