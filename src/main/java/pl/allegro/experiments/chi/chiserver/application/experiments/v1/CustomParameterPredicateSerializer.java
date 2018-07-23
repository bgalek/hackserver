package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.CustomParameterPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PredicateType;

import java.lang.reflect.Type;

public class CustomParameterPredicateSerializer implements JsonSerializer<CustomParameterPredicate> {
    @Override
    public JsonElement serialize(CustomParameterPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", PredicateType.CUSTOM_PARAM.toString());
        element.addProperty("name", src.getName());
        element.addProperty("value", src.getValue());
        return element;
    }
}
