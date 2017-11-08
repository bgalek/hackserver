package pl.allegro.experiments.chi.chiserver.interactions.v1

import com.google.gson.*
import pl.allegro.experiments.chi.chiserver.interactions.Interaction
import java.lang.reflect.Type
import java.time.Instant


class InteractionTypeAdapter: JsonDeserializer<Interaction> {
    override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?): Interaction {
        val jsonObj = json.asJsonObject
        return Interaction(
                if (jsonObj.get("userId") == null || jsonObj.get("userId").isJsonNull) null else jsonObj.get("userId").asString,
                if (jsonObj.get("userCmId") == null || jsonObj.get("userCmId").isJsonNull) null else jsonObj.get("userCmId").asString,
                jsonObj.get("experimentId").asString,
                jsonObj.get("variantName").asString,
                if (jsonObj.get("internal") == null || jsonObj.get("internal").isJsonNull) null else jsonObj.get("internal").asBoolean,
                if (jsonObj.get("deviceClass") == null || jsonObj.get("deviceClass").isJsonNull) null else jsonObj.get("deviceClass").asString,
                Instant.parse(jsonObj.get("interactionDate").asString)
        )
    }
}