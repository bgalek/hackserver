package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.domain.CmuidRegexpExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.HashRangeExperimentVariant


internal class JsonConverter {
     private val gson: Gson = GsonBuilder()
             .registerTypeAdapter(Experiment::class.java, ExperimentFactory.ExperimentDeserializer())
             .registerTypeAdapter(HashRangeExperimentVariant::class.java, ExperimentFactory.HashRangeExperimentVariantDeserializer())
             .registerTypeAdapter(CmuidRegexpExperimentVariant::class.java, ExperimentFactory.CmuidRegexpExperimentVariantDeserializer())
             .registerTypeAdapter(ExperimentVariant::class.java, ExperimentFactory.InternalExperimentVariantDeserializer())
             .registerTypeAdapter(ExperimentVariant::class.java, ExperimentFactory.ExperimentVariantDeserializer())
             .create()

    fun fromJSON(json: String): List<Experiment> {
        return gson.fromJson<List<Experiment>>(json, object : TypeToken<List<Experiment>>() {}.type)
    }
}
