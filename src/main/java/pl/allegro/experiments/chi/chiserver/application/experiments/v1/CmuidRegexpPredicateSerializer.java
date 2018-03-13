package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CmuidRegexpPredicate;

import java.lang.reflect.Type;

public class CmuidRegexpPredicateSerializer implements JsonSerializer<CmuidRegexpPredicate> {
    private static final String CMUID_REGEXP_TYPE = "CMUID_REGEXP";

    @Override
    public JsonElement serialize(CmuidRegexpPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", CMUID_REGEXP_TYPE);
        element.addProperty("regexp", src.getPattern().toString());
        return element;
    }
}
