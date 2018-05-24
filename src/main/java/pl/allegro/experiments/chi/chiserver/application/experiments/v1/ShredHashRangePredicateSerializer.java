package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PredicateType;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.lang.reflect.Type;

public class ShredHashRangePredicateSerializer implements JsonSerializer<ShredHashRangePredicate> {
    @Override
    public JsonElement serialize(ShredHashRangePredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", PredicateType.SHRED_HASH.toString());
        element.add("ranges", context.serialize(src.getRanges()));
        element.addProperty("salt", src.getSalt());

        return element;
    }
}