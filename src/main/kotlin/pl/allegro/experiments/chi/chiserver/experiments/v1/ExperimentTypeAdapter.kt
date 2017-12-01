package pl.allegro.experiments.chi.chiserver.experiments.v1

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentVariant
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ExperimentTypeAdapter : JsonSerializer<Experiment>, JsonDeserializer<Experiment> {

    internal val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun serialize(src: Experiment, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()

        element.add("id", context.serialize(src.id))

        src.activeFrom?.let {
            element.addProperty("activeFrom", src.activeFrom.format(formatter))
        }
        src.activeTo?.let {
            element.addProperty("activeTo", src.activeTo.format(formatter))
        }

        element.add("variants", context.serialize(src.variants))

        return element
    }

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
        return if (dateTime == null) {
            null
        } else ZonedDateTime.parse(dateTime.asString, formatter)
    }
}
