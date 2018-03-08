package pl.allegro.experiments.chi.chiserver.application.experiments.v1;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.DeviceClassPredicate;

import java.lang.reflect.Type;

public class DeviceClassPredicateSerializer implements JsonSerializer<DeviceClassPredicate> {
    private static final String DEVICE_CLASS_TYPE = "DEVICE_CLASS";

    @Override
    public JsonElement serialize(DeviceClassPredicate src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        element.addProperty("type", DEVICE_CLASS_TYPE);
        element.addProperty("device", src.getDevice());
        return element;
    }
}
