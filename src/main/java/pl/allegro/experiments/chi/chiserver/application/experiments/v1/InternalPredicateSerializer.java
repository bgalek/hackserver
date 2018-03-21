package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.InternalPredicate;

import java.lang.reflect.Type;

public class InternalPredicateSerializer implements JsonSerializer<InternalPredicate> {
    private static final String INTERNAL_TYPE = "INTERNAL";

    @Override
    public JsonElement serialize(InternalPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", INTERNAL_TYPE);
        return element;
    }
}