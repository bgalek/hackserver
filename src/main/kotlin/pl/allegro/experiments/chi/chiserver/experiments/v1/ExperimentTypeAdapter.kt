package pl.allegro.experiments.chi.chiserver.experiments.v1

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentVariant
import java.lang.reflect.Type
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ExperimentTypeAdapter : JsonSerializer<Experiment>, JsonDeserializer<Experiment> {

    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    override fun serialize(src: Experiment, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val element = JsonObject()

        element.add("id", context.serialize(src.id))

        src.activeFrom?.let {
            element.addProperty("activeFrom", src.activeFrom.format(formatter))
        }
        src.activeTo?.let {
            element.addProperty("activeTo", src.activeTo.format(formatter))
        }


        element.addProperty("description", src.description)
        element.addProperty("owner", src.owner)
        element.addProperty("reported", src.reported)
        element.add("variants", context.serialize(src.variants))

        return element
    }

    @Throws(JsonParseException::class)
    override fun deserialize(jsonElement: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Experiment {
        val json = jsonElement.asJsonObject

        val id = json.get("id").asString
        val activeFrom = json.get("activeFrom")?.let { ZonedDateTime.parse(it.asString, formatter) }
        val activeTo = json.get("activeTo")?.let { ZonedDateTime.parse(it.asString, formatter) }
        val description = json.get("description")?.asString
        val owner = json.get("owner")?.asString
        val reported = json.get("reported")?.asBoolean?:true
        val variants = context.deserialize<List<ExperimentVariant>>(json.get("variants"), object : TypeToken<List<ExperimentVariant>>() {}.type)
        return Experiment(id, variants, description, owner, reported, activeFrom, activeTo)
    }
}
