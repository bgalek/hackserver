package pl.allegro.experiments.chi.chiserver.experiments.v1

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import pl.allegro.experiments.chi.domain.ExperimentVariant
import java.lang.reflect.Type

class ExperimentVariantTypeAdapter : JsonSerializer<ExperimentVariant> {

    override fun serialize(src: ExperimentVariant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()
        element.addProperty("name", src.name)
        element.add("predicates", context.serialize(src.predicates))
        return element
    }
}