package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CmuidRegexpPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PredicateType;

import java.lang.reflect.Type;

public class CmuidRegexpPredicateSerializer implements JsonSerializer<CmuidRegexpPredicate> {
    @Override
    public JsonElement serialize(CmuidRegexpPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", PredicateType.CMUID_REGEXP.toString());
        element.addProperty("regexp", src.getPattern().toString());
        return element;
    }
}
