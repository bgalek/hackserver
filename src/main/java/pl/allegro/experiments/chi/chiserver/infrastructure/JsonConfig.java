package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JsonConfig {

    static class LocalDateTypeSerializer implements JsonSerializer<LocalDate> {
        @Override
        public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(DateTimeFormatter.ISO_LOCAL_DATE.format(src));
        }
    }

    static class ZonedDateTimeTypeAdapter implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
        @Override
        public JsonElement serialize(ZonedDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(DateTimeFormatter.ISO_DATE_TIME.format(src));
        }

        @Override
        public ZonedDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return ZonedDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME);
        }
    }

    static class DurationTypeAdapter implements JsonSerializer<Duration> {
        @Override
        public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toMillis());
        }
    }

    @Bean
    public Gson jsonConverter() {
        return new GsonBuilder()
                .registerTypeAdapter(Experiment.class, new ExperimentTypeDeserializer())
                .registerTypeAdapter(Experiment.class, new ExperimentTypeSerializer())
                .registerTypeAdapter(HashRangePredicate.class, new HashRangePredicateSerializer())
                .registerTypeAdapter(CmuidRegexpPredicate.class, new CmuidRegexpPredicateSerializer())
                .registerTypeAdapter(InternalPredicate.class, new InternalPredicateSerializer())
                .registerTypeAdapter(DeviceClassPredicate.class, new DeviceClassPredicateSerializer())
                .registerTypeAdapter(ExperimentVariant.class, new ExperimentVariantTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeSerializer())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }
}
