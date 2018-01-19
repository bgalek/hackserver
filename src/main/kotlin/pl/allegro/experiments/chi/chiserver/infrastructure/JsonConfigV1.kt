package pl.allegro.experiments.chi.chiserver.infrastructure

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.github.salomonbrys.kotson.string
import com.github.salomonbrys.kotson.toJson
import com.google.gson.*
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.*
import pl.allegro.experiments.chi.chiserver.domain.experiments.*
import java.lang.reflect.Type
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Configuration
class JsonConfigV1 {

    @Bean
    fun jsonConverterV1(): JsonConverter = GsonBuilder()
            .registerTypeAdapter(experimentDeserializer)
            .registerTypeAdapter(Experiment::class.java, ExperimentSerializerV1())
            .registerTypeAdapter(HashRangePredicate::class.java, HashRangePredicateSerializer())
            .registerTypeAdapter(CmuidRegexpPredicate::class.java, CmuidRegexpPredicateSerializer())
            .registerTypeAdapter(InternalPredicate::class.java, InternalPredicateSerializer())
            .registerTypeAdapter(DeviceClassPredicate::class.java, DeviceClassPredicateSerializer())
            .registerTypeAdapter(ExperimentVariant::class.java, ExperimentVariantTypeAdapter())
            .registerTypeAdapter<LocalDate> {
                serialize { (src) -> DateTimeFormatter.ISO_LOCAL_DATE.format(src).toJson() }
            }
            .registerTypeAdapter<ZonedDateTime> {
                serialize { (src) -> DateTimeFormatter.ISO_DATE_TIME.format(src).toJson() }
                deserialize { (json) -> ZonedDateTime.parse(json.string, DateTimeFormatter.ISO_DATE_TIME) }
            }
            .registerTypeAdapter<Duration> { serialize { d -> d.src.toMillis().toJson() } }
            .create()

    class ExperimentSerializerV1 : JsonSerializer<Experiment> {
        override fun serialize(src: Experiment, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val element = JsonObject()
            element.addProperty("id", src.id)
            element.addProperty("description", src.description)
            element.addProperty("owner", src.author)
            element.addProperty("reportingEnabled", src.reportingEnabled)
            element.add("variants", context.serialize(src.variants))
            if (src.activityPeriod != null) {
                element.add("activeFrom", context.serialize(src.activityPeriod.activeFrom))
                element.add("activeTo", context.serialize(src.activityPeriod.activeTo))
            }
            return element
        }
    }
}