package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.springframework.core.convert.converter.Converter
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.util.regex.Pattern


internal object experimentDeserializer : Converter<DBObject, Experiment> {
    override fun convert(bson: DBObject): Experiment {
        val experiment = Experiment(
            bson["_id"] as String,
            (bson["variants"] as List<DBObject>).map { experimentVariantDeserializer.convert(it) },
            bson["description"] as String?,
            bson["author"] as String?,
            bson["groups"] as List<String>,
            bson["reportingEnabled"] as Boolean? ?: true,
            (bson["activityPeriod"] as BasicDBObject?)?.let { activityPeriodDeserializer.convert(it) }
        )

        return experiment
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
            author?.let { bson["author"] = it }
            bson["groups"] = groups
            reportingEnabled.let { bson["reportingEnabled"] = it }
            activityPeriod?.let { bson["activityPeriod"] = BasicDBObject(
                mapOf(
                    "activeFrom" to dateTimeSerializer.convert(it.activeFrom),
                    "activeTo" to dateTimeSerializer.convert(it.activeTo)
                )
            ) }

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
