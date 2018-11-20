package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTag;

import java.lang.reflect.Type;

public class ExperimentTagSerializer implements JsonSerializer<ExperimentTag> {

    @Override
    public JsonElement serialize(ExperimentTag src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.addProperty("id", src.getId());
        return result;
    }
}
