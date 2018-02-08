package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.PermissionsAwareExperimentGetter

@Configuration
class PermissionsAwareExperimentGetterConfig {
    @Bean
    fun permissionsAwareExperimentGetter(experimentsRepository: ExperimentsRepository, userProvider: UserProvider):
            PermissionsAwareExperimentGetter {
        return PermissionsAwareExperimentGetter(experimentsRepository, userProvider)
    }
}