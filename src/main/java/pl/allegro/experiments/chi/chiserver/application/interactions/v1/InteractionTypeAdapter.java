package pl.allegro.experiments.chi.chiserver.application.interactions.v1;

import com.google.gson.*;
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;

import java.lang.reflect.Type;
import java.time.Instant;

import static pl.allegro.experiments.chi.chiserver.application.JsonObjectGetters.getAsBooleanOrNull;
import static pl.allegro.experiments.chi.chiserver.application.JsonObjectGetters.getAsStringOrNull;

public class InteractionTypeAdapter implements JsonDeserializer<Interaction> {

    @Override
    public Interaction deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();

        return new Interaction(
                getAsStringOrNull("userId", jsonObj),
                getAsStringOrNull("userCmId", jsonObj),
                jsonObj.get("experimentId").getAsString(),
                jsonObj.get("variantName").getAsString(),
                getAsBooleanOrNull("internal", jsonObj),
                getAsStringOrNull("deviceClass", jsonObj),
                Instant.parse(jsonObj.get("interactionDate").getAsString()),
                getAsStringOrNull("appId", jsonObj)
        );
    }

}