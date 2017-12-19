package pl.allegro.experiments.chi.chiserver.infrastructure

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.ExperimentTypeAdapter
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.ExperimentVariantTypeAdapter

internal class JsonParser {
    private val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Experiment::class.java, ExperimentTypeAdapter())
            .registerTypeAdapter(ExperimentVariant::class.java, ExperimentVariantTypeAdapter())
            .create()

    fun fromJSON(json: String): List<Experiment>? {
        return gson.fromJson<List<Experiment>>(json, object : TypeToken<List<Experiment>>() {}.type)
    }
}
