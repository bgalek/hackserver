package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.FullOnPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PredicateType;

import java.lang.reflect.Type;

public class FullOnPredicateSerializer implements JsonSerializer<FullOnPredicate> {
    @Override
    public JsonElement serialize(FullOnPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();
        element.addProperty("type", PredicateType.FULL_ON.toString());
        return element;
    }
}
