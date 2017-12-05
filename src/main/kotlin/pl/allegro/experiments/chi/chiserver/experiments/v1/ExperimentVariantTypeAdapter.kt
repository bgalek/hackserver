package pl.allegro.experiments.chi.chiserver.experiments.v1

import com.google.gson.*
import pl.allegro.experiments.chi.chiserver.domain.*
import java.lang.reflect.Type
import java.util.ArrayList
import java.util.regex.Pattern

class ExperimentVariantTypeAdapter : JsonSerializer<ExperimentVariant>, JsonDeserializer<ExperimentVariant> {

    private val CMUID_REGEXP_TYPE = "CMUID_REGEXP"
    private val HASH_TYPE = "HASH"
    private val INTERNAL_TYPE = "INTERNAL"
    private val DEVICE_CLASS_TYPE = "DEVICE_CLASS"

    override fun serialize(src: ExperimentVariant, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()
        element.addProperty("name", src.name)
        element.add("predicates", context.serialize(src.getPredicates()))
        return element
    }

    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ExperimentVariant {
        val json = jsonElement.asJsonObject

        val id = json.get("name").asString
        return ExperimentVariant(id, deserializePredicates(json))
    }

    private fun deserializePredicates(json: JsonObject): List<Predicate> {
        val predicates = json.get("predicates").asJsonArray.iterator()
        val result = ArrayList<Predicate>()
        while (predicates.hasNext()) {
            result.add(this.deserializePredicate(predicates.next() as JsonObject))
        }
        return result
    }

    private fun deserializePredicate(json: JsonObject): Predicate {
        val type = json.get("type").asString

        if (DEVICE_CLASS_TYPE == type) {
            return deserializeDevicePredicate(json)
        } else if (HASH_TYPE == type) {
            return deserializeHashPredicate(json)
        } else if (CMUID_REGEXP_TYPE == type) {
            return deserializeCmuidPredicate(json)
        } else if (INTERNAL_TYPE == type) {
            return deserializeInternalVariant(json)
        }

        throw IllegalArgumentException("Cannot handle ExperimentInteraction type : " + type)
    }

    private fun deserializeHashPredicate(json: JsonObject): Predicate {
        val from = json.get("from").asInt
        val to = json.get("to").asInt
        return HashRangePredicate(PercentageRange(from, to))
    }

    private fun deserializeInternalVariant(json: JsonObject): Predicate {
        return InternalPredicate()
    }

    private fun deserializeCmuidPredicate(json: JsonObject): Predicate {
        val regexp = json.get("regexp").asString
        return CmuidRegexpPredicate(Pattern.compile(regexp))
    }

    private fun deserializeDevicePredicate(json: JsonObject): Predicate {
        val device = json.get("device").asString
        return DeviceClassPredicate(device)
    }
}
