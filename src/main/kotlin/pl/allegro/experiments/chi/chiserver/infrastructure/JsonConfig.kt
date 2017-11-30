package pl.allegro.experiments.chi.chiserver.infrastructure

import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.experiments.v1.*
import pl.allegro.experiments.chi.chiserver.experiments.v1.ExperimentTypeAdapter
import pl.allegro.experiments.chi.chiserver.experiments.v1.ExperimentVariantTypeAdapter
import pl.allegro.experiments.chi.chiserver.statistics.ExperimentStatistics
import pl.allegro.experiments.chi.domain.*
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Configuration
class JsonConfig {
    @Bean
    fun jsonConverter() = JsonConverter()
}

class JsonConverter {
    private val gson: Gson

    init {
        gson = GsonBuilder()
                .registerTypeAdapter(Experiment::class.java, ExperimentTypeAdapter())
                .registerTypeAdapter(HashRangePredicate::class.java, HashRangePredicateSerializer())
                .registerTypeAdapter(CmuidRegexpPredicate::class.java, CmuidRegexpPredicateSerializer())
                .registerTypeAdapter(InternalPredicate::class.java, InternalPredicateSerializer())
                .registerTypeAdapter(DeviceClassPredicate::class.java, DeviceClassPredicateSerializer())
                .registerTypeAdapter(ExperimentVariant::class.java, ExperimentVariantTypeAdapter())
                .registerTypeAdapter<LocalDate> {
                    serialize { date -> DateTimeFormatter.ISO_LOCAL_DATE.format(date.src).toJson() }
                }
                .registerTypeAdapter<Duration> { serialize { d -> d.src.toMillis().toJson() } }
                .create()
    }

    fun toJSON(experiments: List<Experiment>): String = gson.toJson(experiments)

    fun toJSON(experiment: Experiment): String = gson.toJson(experiment)

    fun toJson(statistics: ExperimentStatistics): String = gson.toJson(statistics)

    fun fromJson(json: String) = gson.fromJson<JsonElement>(json)
}
