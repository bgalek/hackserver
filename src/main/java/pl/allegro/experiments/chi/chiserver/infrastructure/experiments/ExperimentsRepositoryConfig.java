package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import com.google.gson.Gson;
import io.micrometer.core.instrument.MeterRegistry;
import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import pl.allegro.experiments.chi.chiserver.application.experiments.AllEnabledCrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.application.experiments.CrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.application.experiments.WhitelistCrisisManagementFilter;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentTagRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.MeasurementsRepository;
import pl.allegro.experiments.chi.chiserver.infrastructure.druid.DruidClient;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.converters.*;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ExperimentsRepositoryConfig {
    private static final String EXPERIMENTS_COUNT_ALL_METRIC =  "experiments.count.all";
    private static final String EXPERIMENTS_COUNT_ACTIVE_METRIC = "experiments.count.active";
    private static final String EXPERIMENTS_COUNT_DRAFT_METRIC = "experiments.count.draft";

    @Bean(name = "MongoCustomConversions")
    MongoCustomConversions customConversions() {
        List<Converter> converters = new ArrayList<>();
        converters.add(new DateTimeDeserializer());
        converters.add(new DateTimeSerializer());
        converters.add(new ReportingDefinitionDeserializer());
        converters.add(new DeviceClassSerializer());
        converters.add(new DeviceClassDeserializer());
        converters.add(new ExperimentTagDeserializer());
        converters.add(new ExperimentTagSerializer());
        return new MongoCustomConversions(converters);
    }

    @Bean
    ExperimentsRepository experimentsRepository(
            MongoExperimentsRepository mongoExperimentsRepository,
            MeterRegistry metricRegistry) {

        var repository = new CachedExperimentsRepository(mongoExperimentsRepository);

        metricRegistry.gauge(EXPERIMENTS_COUNT_ALL_METRIC, repository,
                r -> r.getAll().size());
        metricRegistry.gauge(EXPERIMENTS_COUNT_ACTIVE_METRIC, repository,
                r -> r.getAll().stream().filter(ExperimentDefinition::isActive).count());
        metricRegistry.gauge(EXPERIMENTS_COUNT_DRAFT_METRIC, repository,
                r -> r.getAll().stream().filter(ExperimentDefinition::isDraft).count());

        return repository;
    }

    @Bean
    ExperimentTagRepository experimentTagRepository(
            MongoExperimentTagRepository mongoExperimentTagRepository) {
        return mongoExperimentTagRepository;
    }

    @Bean
    MeasurementsRepository measurementsRepository(
            DruidClient druid,
            Gson jsonConverter,
            @Value("${druid.experimentsCube}") String datasource) {
        return new DruidMeasurementsRepository(druid, jsonConverter, datasource);
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
    ExperimentGroupRepository cachedExperimentGroupRepository(
            MongoExperimentGroupRepository mongoExperimentGroupRepository,
            Javers javers,
            UserProvider userProvider,
            ExperimentsMongoMetricsReporter metrics) {
        return new CachedExperimentGroupRepository(mongoExperimentGroupRepository, javers, userProvider, metrics);
    }
}
