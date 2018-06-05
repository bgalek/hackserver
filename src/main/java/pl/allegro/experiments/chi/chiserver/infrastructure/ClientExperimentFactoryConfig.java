package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

@Configuration
public class ClientExperimentFactoryConfig {

    @Bean
    ClientExperimentFactory clientExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository) {
        return new ClientExperimentFactory(experimentGroupRepository, experimentsRepository);
    }
}