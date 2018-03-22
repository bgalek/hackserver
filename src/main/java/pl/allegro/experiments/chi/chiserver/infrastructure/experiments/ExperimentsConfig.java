package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.gson.Gson;
import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.web.client.RestTemplate;
import pl.allegro.experiments.chi.chiserver.application.experiments.AllEnabledCrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.application.experiments.CrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.application.experiments.WhitelistCrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.DateTimeDeserializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.DateTimeSerializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ExperimentDeserializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ExperimentSerializer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ExperimentsConfig {
    private static final String EXPERIMENTS_COUNT_ALL_METRIC =  "experiments.count.all";
    private static final String EXPERIMENTS_COUNT_LIVE_METRIC = "experiments.count.live";

    @Bean
    FileBasedExperimentsRepository fileBasedExperimentsRepository(
            @Value("${chi.experiments.file}") String jsonUrl,
            RestTemplate restTemplate,
            Gson jsonConverter) {
        HttpContentLoader httpContentLoader = new HttpContentLoader(restTemplate);
        return new FileBasedExperimentsRepository(jsonUrl, httpContentLoader, jsonConverter, null);
    }

    @Bean
    CustomConversions customConversions(
            DateTimeSerializer dateTimeSerializer,
            DateTimeDeserializer dateTimeDeserializer,
            ExperimentSerializer experimentSerializer,
            ExperimentDeserializer experimentDeserializer) {
        List<Converter> converters = new ArrayList<>();
        converters.add(dateTimeDeserializer);
        converters.add(dateTimeSerializer);
        converters.add(experimentSerializer);
        converters.add(experimentDeserializer);
        return new CustomConversions(converters);
    }

    @Bean
    MongoExperimentsRepository mongoExperimentsRepository(
            MongoTemplate mongoTemplate,
            ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter,
            Javers javers,
            UserProvider userProvider) {
        return new MongoExperimentsRepository(mongoTemplate, experimentsMongoMetricsReporter, javers, userProvider);
    }

    @Bean
    ExperimentsRepository experimentsRepository(
            FileBasedExperimentsRepository fileBasedExperimentsRepository,
            CachedExperimentsRepository mongoExperimentsRepository,
            MetricRegistry metricRegistry) {
        ExperimentsRepository repo = new ExperimentsDoubleRepository(fileBasedExperimentsRepository, mongoExperimentsRepository);
        Gauge<Integer> gaugeAll = () -> repo.getAll().size();
        metricRegistry.register(EXPERIMENTS_COUNT_ALL_METRIC, gaugeAll);

        Gauge<Integer> gaugeLive = () -> (int)repo.getAll().stream().count();

        metricRegistry.register(EXPERIMENTS_COUNT_LIVE_METRIC, gaugeLive);
        return repo;
    }

    @Bean
    MeasurementsRepository measurementsRepository(
            DruidClient druid,
            Gson jsonConverter,
            @Value("${druid.experimentsCube}") String datasource) {
        return new DruidMeasurementsRepository(druid, jsonConverter, datasource);
    }

    @Bean
    FileBasedExperimentsRepositoryRefresher fileBasedExperimentsRepositoryRefresher(
            FileBasedExperimentsRepository fileBasedExperimentsRepository) {
        return new FileBasedExperimentsRepositoryRefresher(fileBasedExperimentsRepository);
    }

    @Bean
    CachedExperimentsRepository mongoRefresher(MongoExperimentsRepository experimentsRepository) {
        return new CachedExperimentsRepository(experimentsRepository);
    }

    @Bean
    ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter(MetricRegistry metricRegistry) {
        return new ExperimentsMongoMetricsReporter(metricRegistry);
    }

    @Bean
    CrisisManagementFilter crisisManagementFilter(CrisisManagementProperties crisisManagementProperties) {
        if (crisisManagementProperties.getEnabled()) {
            return new WhitelistCrisisManagementFilter(crisisManagementProperties.getWhiteList());
        } else {
            return new AllEnabledCrisisManagementFilter();
        }
    }

    @Bean
    @ConfigurationProperties("chi.crisisManagement")
    CrisisManagementProperties crisisManagementProperties() {
        return new CrisisManagementProperties();
    }
}
