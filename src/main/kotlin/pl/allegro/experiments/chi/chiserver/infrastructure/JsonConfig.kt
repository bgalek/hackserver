package pl.allegro.experiments.chi.chiserver.infrastructure

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.string
import com.github.salomonbrys.kotson.toJson
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

typealias JsonConverter = Gson

@Configuration
class JsonConfig {
    @Bean
    fun jsonConverter(): JsonConverter = GsonBuilder()
            .registerTypeAdapter(Experiment::class.java, ExperimentTypeDeserializer())
            .registerTypeAdapter(Experiment::class.java, ExperimentTypeSerializer())
            .registerTypeAdapter(HashRangePredicate::class.java, HashRangePredicateSerializer())
            .registerTypeAdapter(CmuidRegexpPredicate::class.java, CmuidRegexpPredicateSerializer())
            .registerTypeAdapter(InternalPredicate::class.java, InternalPredicateSerializer())
            .registerTypeAdapter(DeviceClassPredicate::class.java, DeviceClassPredicateSerializer())
            .registerTypeAdapter(ExperimentVariant::class.java, ExperimentVariantTypeAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeSerializer())
            .registerTypeAdapter<LocalDate> {
                serialize { (src) -> DateTimeFormatter.ISO_LOCAL_DATE.format(src).toJson() }
            }
            .registerTypeAdapter<ZonedDateTime> {
                serialize { (src) -> DateTimeFormatter.ISO_DATE_TIME.format(src).toJson() }
                deserialize { (json) -> ZonedDateTime.parse(json.string, DateTimeFormatter.ISO_DATE_TIME) }
            }
            .registerTypeAdapter<Duration> { serialize { d -> d.src.toMillis().toJson() } }
            .create()
}
