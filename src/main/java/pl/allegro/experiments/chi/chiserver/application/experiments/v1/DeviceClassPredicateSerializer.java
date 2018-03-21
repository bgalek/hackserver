package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClassPredicate;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PredicateType;

import java.lang.reflect.Type;

public class DeviceClassPredicateSerializer implements JsonSerializer<DeviceClassPredicate> {
    @Override
    public JsonElement serialize(DeviceClassPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", PredicateType.DEVICE_CLASS.toString());
        element.addProperty("device", src.getDevice());
        return element;
    }
}
