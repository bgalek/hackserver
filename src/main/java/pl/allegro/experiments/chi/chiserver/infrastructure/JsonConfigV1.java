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

@Configuration
public class JsonConfigV1 {

    @Bean
    public Gson jsonConverterV1() {
        return new GsonBuilder()
                .registerTypeAdapter(Experiment.class, new ExperimentTypeDeserializer())
                .registerTypeAdapter(Experiment.class, new ExperimentSerializerV1())
                .registerTypeAdapter(HashRangePredicate.class, new HashRangePredicateSerializer())
                .registerTypeAdapter(CmuidRegexpPredicate.class, new CmuidRegexpPredicateSerializer())
                .registerTypeAdapter(InternalPredicate.class, new InternalPredicateSerializer())
                .registerTypeAdapter(DeviceClassPredicate.class, new DeviceClassPredicateSerializer())
                .registerTypeAdapter(ExperimentVariant.class, new ExperimentVariantTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .create();
    }

    static class ExperimentSerializerV1 implements JsonSerializer<Experiment> {
        @Override
        public JsonElement serialize(Experiment src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject element = new JsonObject();
            element.addProperty("id", src.getId());
            element.addProperty("description", src.getDescription());
            element.addProperty("owner", src.getAuthor());
            element.addProperty("reportingEnabled", src.getReportingEnabled());
            element.add("variants", context.serialize(src.getVariants()));
            if (src.getActivityPeriod() != null) {
                element.add("activeFrom", context.serialize(src.getActivityPeriod().getActiveFrom()));
                element.add("activeTo", context.serialize(src.getActivityPeriod().getActiveTo()));
            }
            return element;
        }
    }
}
