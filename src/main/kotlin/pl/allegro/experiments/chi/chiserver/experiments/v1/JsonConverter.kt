package pl.allegro.experiments.chi.chiserver.experiments.v1

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import pl.allegro.experiments.chi.chiserver.domain.*

class JsonConverter {
    var gson: Gson

    init {
        gson = GsonBuilder()
                .registerTypeAdapter(Experiment::class.java, ExperimentTypeAdapter())
                .registerTypeAdapter(HashRangePredicate::class.java, HashRangePredicateSerializer())
                .registerTypeAdapter(CmuidRegexpPredicate::class.java, CmuidRegexpPredicateSerializer())
                .registerTypeAdapter(InternalPredicate::class.java, InternalPredicateSerializer())
                .registerTypeAdapter(DeviceClassPredicate::class.java, DeviceClassPredicateSerializer())
                .registerTypeAdapter(ExperimentVariant::class.java, ExperimentVariantTypeAdapter())
                .create()
    }

    fun toJSON(experiments: List<Experiment>): String = gson.toJson(experiments)

    fun toJSON(experiment: Experiment): String = gson.toJson(experiment)
}
