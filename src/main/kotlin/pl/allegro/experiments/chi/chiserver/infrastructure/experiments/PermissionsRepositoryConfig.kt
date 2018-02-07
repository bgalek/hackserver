package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.PermissionsRepository

@Configuration
class PermissionsRepositoryConfig {

    @Bean
    fun configurePermissionsRepository(userProvider: UserProvider): PermissionsRepository {
        return PermissionsRepository(userProvider)
    }
}