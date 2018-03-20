package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.PermissionsRepository;

@Configuration
public class PermissionsRepositoryConfig {

    @Bean
    PermissionsRepository permissionsRepository(
            UserProvider userProvider,
            ExperimentsRepository experimentsRepository) {
        return new PermissionsRepository(userProvider, experimentsRepository);
    }
}
