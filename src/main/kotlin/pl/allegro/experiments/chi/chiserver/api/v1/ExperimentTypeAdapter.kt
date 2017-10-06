package pl.allegro.experiments.chi.chiserver.api.v1

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import pl.allegro.experiments.chi.domain.Experiment
import java.lang.reflect.Type
import java.time.format.DateTimeFormatter

class ExperimentTypeAdapter : JsonSerializer<Experiment> {

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

}