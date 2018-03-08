package pl.allegro.experiments.chi.chiserver.application;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonObjectGetters {
    public static String getAsStringOrNull(String property, JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(property);
        return jsonElement != null ? jsonElement.getAsString() : null;
    }

    public static Boolean getAsBooleanOrNull(String property, JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.get(property);
        return jsonElement != null ? jsonElement.getAsBoolean() : null;
    }
}
