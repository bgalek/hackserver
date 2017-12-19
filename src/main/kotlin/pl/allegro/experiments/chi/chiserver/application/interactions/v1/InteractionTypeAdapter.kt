package pl.allegro.experiments.chi.chiserver.application.interactions.v1

import com.google.gson.*
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction
import java.lang.reflect.Type
import java.time.Instant

class InteractionTypeAdapter: JsonDeserializer<Interaction> {
    override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?): Interaction {
        val jsonObj = json.asJsonObject
        return Interaction(
                jsonObj.get("userId")?.asString,
                jsonObj.get("userCmId")?.asString,
                jsonObj.get("experimentId").asString,
                jsonObj.get("variantName").asString,
                jsonObj.get("internal")?.asBoolean,
                jsonObj.get("deviceClass")?.asString,
                Instant.parse(jsonObj.get("interactionDate").asString),
                jsonObj.get("appId")?.asString
        )
    }
}