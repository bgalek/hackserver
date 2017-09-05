package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.domain.*
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


object ExperimentFactory {

    class ExperimentDeserializer : JsonDeserializer<Experiment> {

        private val formatter = DateTimeFormatter.ISO_DATE_TIME

        @Throws(JsonParseException::class)
        override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Experiment {
            val json = jsonElement.asJsonObject

            val id = json.get("id").asString
            val activeFrom = dateTimeFromString(json.get("activeFrom"))
            val activeTo = dateTimeFromString(json.get("activeTo"))

            val variants = context.deserialize<List<ExperimentVariant>>(json.get("variants"), object : TypeToken<List<ExperimentVariant>>() {}.type)
            return Experiment(id, variants, activeFrom, activeTo)
        }

        private fun dateTimeFromString(dateTime: JsonElement?): ZonedDateTime? {
            if (dateTime == null) {
                return null
            }
            return ZonedDateTime.parse(dateTime.asString, formatter)
        }
    }

    class ExperimentVariantDeserializer : JsonDeserializer<ExperimentVariant> {
        @Throws(JsonParseException::class)
        override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ExperimentVariant {
            val json = jsonElement.asJsonObject

            if (!json.has("type")) {
                throw IllegalArgumentException("Cannot handle VariantAssignment with null type")
            }
            val type = json.get("type").asString

            if ("HASH" == type) {
                return context.deserialize<ExperimentVariant>(jsonElement, HashRangeExperimentVariant::class.java)
            } else if ("CMUID_REGEXP" == type) {
                return context.deserialize<ExperimentVariant>(jsonElement, CmuidRegexpExperimentVariant::class.java)
            } else if ("INTERNAL" == type) {
                return context.deserialize<ExperimentVariant>(jsonElement, InternalExperimentVariant::class.java)
            }

            throw IllegalArgumentException("Cannot handle VariantAssignment type : " + type)
        }
    }

    class HashRangeExperimentVariantDeserializer : JsonDeserializer<HashRangeExperimentVariant> {
        @Throws(JsonParseException::class)
        override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): HashRangeExperimentVariant {
            val json = jsonElement.asJsonObject

            val id = json.get("name").asString
            val from = json.get("from").asInt
            val to = json.get("to").asInt
            return HashRangeExperimentVariant(id, PercentageRange(from, to))
        }
    }

    class CmuidRegexpExperimentVariantDeserializer : JsonDeserializer<CmuidRegexpExperimentVariant> {
        @Throws(JsonParseException::class)
        override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): CmuidRegexpExperimentVariant {
            val json = jsonElement.asJsonObject

            val id = json.get("name").asString
            val regexp = json.get("regexp").asString
            return CmuidRegexpExperimentVariant(id, regexp)
        }
    }

    class InternalExperimentVariantDeserializer : JsonDeserializer<InternalExperimentVariant> {
        @Throws(JsonParseException::class)
        override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): InternalExperimentVariant {
            val json = jsonElement.asJsonObject

            val id = json.get("name").asString
            return InternalExperimentVariant(id)
        }
    }
}
