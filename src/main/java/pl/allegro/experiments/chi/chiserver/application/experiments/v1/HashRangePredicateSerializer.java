package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate;

import java.lang.reflect.Type;

public class HashRangePredicateSerializer implements JsonSerializer<HashRangePredicate> {
    private static final String HASH_TYPE = "HASH";

    @Override
    public JsonElement serialize(HashRangePredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", HASH_TYPE);
        element.addProperty("from", src.getHashRange().getFrom());
        element.addProperty("to", src.getHashRange().getTo());
        return element;
    }
}
