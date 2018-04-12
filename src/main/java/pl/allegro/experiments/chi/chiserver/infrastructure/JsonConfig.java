package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Configuration
class JsonConfig {

    @Bean
    Gson jsonConverter() {
        return new GsonBuilder()
                .registerTypeAdapter(Experiment.class, new ExperimentTypeDeserializer())
                .registerTypeAdapter(ClientExperiment.class, new ClientExperimentTypeSerializer())
                .registerTypeAdapter(Experiment.class, new ExperimentTypeSerializer())
                .registerTypeAdapter(ExperimentDefinition.class, new ExperimentDefinitionTypeSerializer())
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
