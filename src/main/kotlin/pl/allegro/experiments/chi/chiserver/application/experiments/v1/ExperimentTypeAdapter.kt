package pl.allegro.experiments.chi.chiserver.application.experiments.v1

import com.github.salomonbrys.kotson.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import com.google.gson.JsonObject



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
    val explicitStatus:ExperimentStatus? =  json["explicitStatus"]?.let{ExperimentStatus.valueOf(it.string)}

    val period: ActivityPeriod? = if (activeFrom != null && activeTo != null) ActivityPeriod(activeFrom, activeTo) else null

    Experiment(id, variants, description, documentLink, owner, emptyList(), reported, period, measurements, null, null, explicitStatus)
}

val experimentSerializer = jsonSerializer<Experiment> jsonObject@ { (experiment, _, context) ->
    val jsonObject = JsonObject()

    jsonObject.addProperty("id", experiment.id)
    jsonObject.addProperty("variants", context.serialize(experiment.variants))
    jsonObject.addProperty("description", experiment.description)
    jsonObject.addProperty("documentLink", experiment.documentLink)
    jsonObject.addProperty("author", experiment.author)
    jsonObject.addProperty("groups", context.serialize(experiment.groups))
    jsonObject.addProperty("reportingEnabled", experiment.reportingEnabled)
    jsonObject.addProperty("activityPeriod", context.serialize(experiment.activityPeriod))
    jsonObject.addProperty("measurements", context.serialize(experiment.measurements))
    jsonObject.addProperty("editable", context.serialize(experiment.editable))
    jsonObject.addProperty("status", context.serialize(experiment.status))
    jsonObject.addProperty("origin", context.serialize(experiment.origin))
    return@jsonObject jsonObject
}