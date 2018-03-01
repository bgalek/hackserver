package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.springframework.core.convert.converter.Converter
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.util.*
import java.util.regex.Pattern

internal object experimentDeserializer : Converter<DBObject, Experiment> {
    override fun convert(bson: DBObject): Experiment {
        val id = bson["_id"] as String
        val variants = (bson["variants"] as List<DBObject>).map { experimentVariantDeserializer.convert(it) }
        val description = bson["description"] as String?
        val documentLink = bson["documentLink"] as String?
        val author = bson["author"] as String?
        val groups = bson["groups"] as List<String>
        val reportingEnabled = bson["reportingEnabled"] as Boolean? ?: true
        val activityPeriod = (bson["activityPeriod"] as BasicDBObject?)?.let { activityPeriodDeserializer.convert(it) }
        val explicitStatus = bson["explicitStatus"]?.let {
            ExperimentStatus.valueOf(it as String)
        }
        return Experiment.builder()
                .id(id)
                .variants(variants)
                .description(description)
                .documentLink(documentLink)
                .author(author)
                .groups(groups)
                .reportingEnabled(reportingEnabled)
                .activityPeriod(activityPeriod)
                .explicitStatus(explicitStatus)
                .build()
    }
}

internal object experimentSerializer : Converter<Experiment, DBObject> {
    override fun convert(source: Experiment): DBObject {
        with(source) {
            val bson = BasicDBObject(
                mapOf(
                    "_id" to id,
                    "variants" to variants.map { experimentVariantSerializer.convert(it) }
                )
            )

            description?.let { bson["description"] = it }
            documentLink?.let { bson["documentLink"] = it }
            author?.let { bson["author"] = it }
            bson["groups"] = groups
            reportingEnabled.let { bson["reportingEnabled"] = it }
            activityPeriod?.let { bson["activityPeriod"] = BasicDBObject(
                mapOf(
                    "activeFrom" to dateTimeSerializer.convert(it.activeFrom),
                    "activeTo" to dateTimeSerializer.convert(it.activeTo)
                )
            ) }
            bson["explicitStatus"] = source.status.explicitOrNull()?.let{ it.toString() }
            return bson
        }
    }
}

internal object experimentVariantDeserializer : Converter<DBObject, ExperimentVariant> {
    override fun convert(bson: DBObject): ExperimentVariant {
        return ExperimentVariant(
            bson["name"] as String,
            (bson["predicates"] as List<DBObject>).map { predicateDeserializer.convert(it) }
        )
    }
}

internal object experimentVariantSerializer : Converter<ExperimentVariant, DBObject> {
    override fun convert(source: ExperimentVariant) = with(source) {
        BasicDBObject(
            mapOf(
                "name" to name,
                "predicates" to predicates.map { it.serialize() }
            )
        )
    }
}

internal object predicateDeserializer : Converter<DBObject, Predicate> {
    override fun convert(bson: DBObject): Predicate {
        val type = bson["type"] ?: throw IllegalArgumentException("No predicate type in: $bson")
        require(type in predicateDeserializers.keys) { "Unknown predicate type in: $bson" }
        return predicateDeserializers[type]!!(bson)
    }

    private val predicateDeserializers = mapOf<String, (DBObject) -> Predicate>(
        "HASH" to { bson -> HashRangePredicate(PercentageRange(bson["from"] as Int, bson["to"] as Int)) },
        "INTERNAL" to { _ -> InternalPredicate() },
        "DEVICE_CLASS" to { bson -> DeviceClassPredicate(bson["device"] as String) },
        "CMUID_REGEXP" to { bson -> CmuidRegexpPredicate(Pattern.compile(bson["regexp"] as String)) }
    )
}

fun Predicate.serialize(): DBObject = when (this) {
    is HashRangePredicate -> BasicDBObject(
        mapOf("type" to "HASH", "from" to hashRange.from, "to" to hashRange.to)
    )
    is InternalPredicate -> BasicDBObject( mapOf("type" to "INTERNAL"))
    is CmuidRegexpPredicate -> BasicDBObject( mapOf("type" to "CMUID_REGEXP", "regexp" to this.pattern.toString()))
    is DeviceClassPredicate -> BasicDBObject (mapOf("type" to "DEVICE_CLASS", "device" to this.device))
    else -> throw UnsupportedOperationException("Can't serialize ${this.javaClass}")
}
