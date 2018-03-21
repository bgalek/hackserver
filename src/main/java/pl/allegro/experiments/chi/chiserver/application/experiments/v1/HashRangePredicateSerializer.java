package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.HashRangePredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PredicateType;

import java.lang.reflect.Type;

public class HashRangePredicateSerializer implements JsonSerializer<HashRangePredicate> {
    @Override
    public JsonElement serialize(HashRangePredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", PredicateType.HASH.toString());
        element.addProperty("from", src.getHashRange().getFrom());
        element.addProperty("to", src.getHashRange().getTo());
        return element;
    }
}
