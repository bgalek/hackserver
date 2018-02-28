package pl.allegro.experiments.chi.chiserver.application.experiments.v1

import com.github.salomonbrys.kotson.bool
import com.github.salomonbrys.kotson.jsonDeserializer
import com.github.salomonbrys.kotson.obj
import com.github.salomonbrys.kotson.string
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val experimentDeserializer = jsonDeserializer { (jsonElement, _, context) ->
    val formatter = DateTimeFormatter.ISO_DATE_TIME
    val json = jsonElement.obj

    val id = json["id"]!!.string
    val activeFrom = json["activeFrom"]?.let { ZonedDateTime.parse(it.string, formatter) }
    val activeTo = json["activeTo"]?.let { ZonedDateTime.parse(it.string, formatter) }
    val description = json["description"]?.string
    val documentLink = json["documentLink"]?.string
    val owner = json["owner"]?.string
    val reported = json["reportingEnabled"]?.bool ?: true
    val variants = context.deserialize<List<ExperimentVariant>>(json["variants"]!!)
    val measurements = json["measurements"]?.let { context.deserialize<ExperimentMeasurements>(json["measurements"]) }
    val explicitStatus:ExperimentStatus? =  json["status"]?.let{ExperimentStatus.valueOf(it.string)}

    val period: ActivityPeriod? = if (activeFrom != null && activeTo != null) ActivityPeriod(activeFrom, activeTo) else null

    Experiment(id, variants, description, documentLink, owner, emptyList(), reported, period, measurements, null, null, explicitStatus)
}