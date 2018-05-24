package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.gson.Gson;
import io.micrometer.core.instrument.MeterRegistry;
import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.web.client.RestTemplate;
import pl.allegro.experiments.chi.chiserver.application.experiments.AllEnabledCrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.application.experiments.CrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.application.experiments.WhitelistCrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.ClientExperimentFactory;
import pl.allegro.experiments.chi.chiserver.infrastructure.statistics.DruidClient;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.DateTimeDeserializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.DateTimeSerializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ExperimentDeserializer;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.ExperimentSerializer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ExperimentsConfig {
    private static final String EXPERIMENTS_COUNT_ALL_METRIC =  "experiments.count.all";
    private static final String EXPERIMENTS_COUNT_ACTIVE_METRIC = "experiments.count.active";
    private static final String EXPERIMENTS_COUNT_DRAFT_METRIC = "experiments.count.draft";

    @Bean
    FileBasedExperimentsRepository fileBasedExperimentsRepository(
            @Value("${chi.experiments.file}") String jsonUrl,
            RestTemplate restTemplate,
            Gson jsonConverter) {
        HttpContentLoader httpContentLoader = new HttpContentLoader(restTemplate);

        return new FileBasedExperimentsRepository(jsonUrl, httpContentLoader, jsonConverter, null);
    }

    @Bean
    MongoCustomConversions customConversions(
            DateTimeSerializer dateTimeSerializer,
            DateTimeDeserializer dateTimeDeserializer,
            ExperimentSerializer experimentSerializer,
            ExperimentDeserializer experimentDeserializer) {
        List<Converter> converters = new ArrayList<>();
        converters.add(dateTimeDeserializer);
        converters.add(dateTimeSerializer);
        converters.add(experimentSerializer);
        converters.add(experimentDeserializer);
        return new MongoCustomConversions(converters);
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
            MeterRegistry metricRegistry) {
        ExperimentsRepository repo = new ExperimentsDoubleRepository(fileBasedExperimentsRepository, mongoExperimentsRepository);

        metricRegistry.gauge(EXPERIMENTS_COUNT_ALL_METRIC, repo,
                r -> r.getAll().size());

        metricRegistry.gauge(EXPERIMENTS_COUNT_ACTIVE_METRIC, repo,
                r -> r.getAll().stream().filter(Experiment::isActive).count());

        metricRegistry.gauge(EXPERIMENTS_COUNT_DRAFT_METRIC, repo,
                r -> r.getAll().stream().filter(Experiment::isDraft).count());

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
    ExperimentsMongoMetricsReporter experimentsMongoMetricsReporter(MeterRegistry metricRegistry) {
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
    @ConfigurationProperties("chi.crisis-management")
    CrisisManagementProperties crisisManagementProperties() {
        return new CrisisManagementProperties();
    }

    @Bean
    ExperimentGroupRepository experimentGroupRepository(
            MongoTemplate mongoTemplate,
            Javers javers,
            UserProvider userProvider) {
        return new MongoExperimentGroupRepository(mongoTemplate, javers, userProvider);
    }

    // todo move to package above
    @Bean
    ClientExperimentFactory clientExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository) {
        return new ClientExperimentFactory(experimentGroupRepository, experimentsRepository);
    }
}
