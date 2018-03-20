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
