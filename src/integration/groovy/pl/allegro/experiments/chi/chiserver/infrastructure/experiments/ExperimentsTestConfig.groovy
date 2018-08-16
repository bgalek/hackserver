package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider

@Configuration
class ExperimentsTestConfig {

    @Primary
    @Bean
    MutableUserProvider mutableUserProvider() {
        return new MutableUserProvider()
    }
}

class MutableUserProvider implements UserProvider {
    static User DEFAULT = new User("Anonymous", [], false)
    User user = DEFAULT

    def setUser(User user) {
        this.user = user
    }

    @Override
    User getCurrentUser() {
        return user
    }
}

