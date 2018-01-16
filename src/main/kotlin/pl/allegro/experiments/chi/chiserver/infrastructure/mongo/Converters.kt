package pl.allegro.experiments.chi.chiserver.infrastructure.mongo

import org.springframework.core.convert.converter.Converter
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
