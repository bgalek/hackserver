package pl.allegro.experiments.chi.chiserver.experiments.v1

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import pl.allegro.experiments.chi.chiserver.domain.CmuidRegexpPredicate
import pl.allegro.experiments.chi.chiserver.domain.DeviceClassPredicate
import pl.allegro.experiments.chi.chiserver.domain.HashRangePredicate
import pl.allegro.experiments.chi.chiserver.domain.InternalPredicate
import java.lang.reflect.Type

class HashRangePredicateSerializer : JsonSerializer<HashRangePredicate> {
    internal val HASH_TYPE = "HASH"

    override fun serialize(src: HashRangePredicate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()

        element.addProperty("type", HASH_TYPE)
        element.addProperty("from", src.hashRange.from)
        element.addProperty("to", src.hashRange.to)
        return element
    }
}

class CmuidRegexpPredicateSerializer : JsonSerializer<CmuidRegexpPredicate> {
    internal val CMUID_REGEXP_TYPE = "CMUID_REGEXP"

    override fun serialize(src: CmuidRegexpPredicate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()

        element.addProperty("type", CMUID_REGEXP_TYPE)
        element.addProperty("regexp", src.pattern.toString())
        return element
    }
}

class DeviceClassPredicateSerializer : JsonSerializer<DeviceClassPredicate> {
    internal val DEVICE_CLASS_TYPE = "DEVICE_CLASS"

    override fun serialize(src: DeviceClassPredicate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()

        element.addProperty("type", DEVICE_CLASS_TYPE)
        element.addProperty("device", src.device.toString())
        return element
    }
}

class InternalPredicateSerializer : JsonSerializer<InternalPredicate> {
    internal val INTERNAL_TYPE = "INTERNAL"

    override fun serialize(src: InternalPredicate, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()

        element.addProperty("type", INTERNAL_TYPE)
        return element
    }
}