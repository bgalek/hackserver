package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.application.experiments.v1.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.client.ClientExperiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;
import pl.allegro.experiments.chi.chiserver.domain.statistics.bayes.BayesianExperimentStatisticsForVariant;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatistics;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.ClassicExperimentStatisticsForVariantMetric;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.VariantStatistics;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Configuration
class JsonConfig {

    @Bean
    Gson jsonConverter() {
        return new GsonBuilder()
                .registerTypeAdapter(BayesianExperimentStatisticsForVariant.class, new BayesianExperimentStatisticsDeserializer())
                .registerTypeAdapter(VariantStatistics.class, new VariantStatisticsDeserializer())
                .registerTypeAdapter(ClassicExperimentStatisticsForVariantMetric.class, new ClassicExperimentStatisticsForVariantMetricDeserializer())
                .registerTypeAdapter(ClassicExperimentStatistics.class, new ClassicExperimentStatisticsDeserializer())
                .registerTypeAdapter(ClientExperiment.class, new ClientExperimentTypeSerializer())
                .registerTypeAdapter(AdminExperiment.class, new AdminExperimentTypeSerializer())
                .registerTypeAdapter(HashRangePredicate.class, new HashRangePredicateSerializer())
                .registerTypeAdapter(ShredHashRangePredicate.class, new ShredHashRangePredicateSerializer())
                .registerTypeAdapter(CustomParameterPredicate.class, new CustomParameterPredicateSerializer())
                .registerTypeAdapter(ExperimentGroup.class, new ExperimentGroupTypeSerializer())
                .registerTypeAdapter(InternalPredicate.class, new InternalPredicateSerializer())
                .registerTypeAdapter(FullOnPredicate.class, new FullOnPredicateSerializer())
                .registerTypeAdapter(DeviceClassPredicate.class, new DeviceClassPredicateSerializer())
                .registerTypeAdapter(ExperimentVariant.class, new ExperimentVariantTypeAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter())
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .registerTypeAdapter(Duration.class, new DurationTypeAdapter())
                .registerTypeAdapter(ExperimentTag.class, new ExperimentTagSerializer())
                .create();
    }
}
