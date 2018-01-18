package pl.allegro.experiments.chi.chiserver.infrastructure.mongo

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
            bson["owner"] as String?,
            bson["reportingEnabled"] as Boolean? ?: true,
            (bson["activeFrom"] as String?)?.let { dateTimeDeserializer.convert(it) },
            (bson["activeTo"] as String?)?.let { dateTimeDeserializer.convert(it) }
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
            owner?.let { bson["owner"] = it }
            reportingEnabled.let { bson["reportingEnabled"] = it }
            activeFrom?.let { bson["activeFrom"] = dateTimeSerializer.convert(it) }
            activeTo?.let { bson["activeTo"] = dateTimeSerializer.convert(it) }

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
    else -> throw UnsupportedOperationException("Can't serialize ${this.javaClass}")
}