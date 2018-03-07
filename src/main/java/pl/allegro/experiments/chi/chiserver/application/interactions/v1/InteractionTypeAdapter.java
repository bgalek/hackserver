package pl.allegro.experiments.chi.chiserver.application.interactions.v1;

import com.google.gson.*;
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;

import java.lang.reflect.Type;
import java.time.Instant;

public class InteractionTypeAdapter implements JsonDeserializer<Interaction> {

    @Override
    public Interaction deserialize(
            JsonElement json,
            Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObj = json.getAsJsonObject();

        JsonElement rawUserId = jsonObj.get("userId");
        JsonElement rawUserCmId = jsonObj.get("userCmId");
        JsonElement rawInternal = jsonObj.get("internal");
        JsonElement rawDeviceClass = jsonObj.get("deviceClass");
        JsonElement rawAppId = jsonObj.get("appId");

        return new Interaction(
                rawUserId != null ? rawUserId.getAsString(): null,
                rawUserCmId != null ? rawUserCmId.getAsString(): null,
                jsonObj.get("experimentId").getAsString(),
                jsonObj.get("variantName").getAsString(),
                rawInternal != null ? rawInternal.getAsBoolean(): null,
                rawDeviceClass != null ? rawDeviceClass.getAsString(): null,
                Instant.parse(jsonObj.get("interactionDate").getAsString()),
                rawAppId != null ? rawAppId.getAsString(): null
        );
    }
}
