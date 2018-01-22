package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import com.mongodb.BasicDBObject
import org.springframework.core.convert.converter.Converter
import pl.allegro.experiments.chi.chiserver.domain.experiments.ActivityPeriod
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val mongoConverters = listOf(
        dateTimeSerializer,
        dateTimeDeserializer,
        experimentDeserializer,
        experimentSerializer
)

private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

object dateTimeDeserializer : Converter<String, ZonedDateTime> {
    override fun convert(source: String) = ZonedDateTime.parse(source, dateFormatter)
}

object dateTimeSerializer : Converter<ZonedDateTime, String> {
    override fun convert(source: ZonedDateTime) = source.format(dateFormatter)
}

object activityPeriodDeserializer : Converter<BasicDBObject, ActivityPeriod> {
    override fun convert(source: BasicDBObject): ActivityPeriod {
        val from = source.get("activeFrom") as String
        val to = source.get("activeTo") as String
        return ActivityPeriod(dateTimeDeserializer.convert(from), dateTimeDeserializer.convert(to))
    }
}

object activityPeriodSerializer : Converter<ActivityPeriod, BasicDBObject> {
    override fun convert(source: ActivityPeriod): BasicDBObject {
        val result = BasicDBObject()
        result.append("activeFrom", dateTimeSerializer.convert(source.activeFrom))
        result.append("activeTo", dateTimeSerializer.convert(source.activeTo))
        return result;
    }
}