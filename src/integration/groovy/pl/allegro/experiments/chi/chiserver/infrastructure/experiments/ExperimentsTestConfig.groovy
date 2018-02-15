package pl.allegro.experiments.chi.chiserver.infrastructure.experiments

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.UserProvider
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository

@Configuration
class ExperimentsTestConfig {
    static String TEST_EXPERIMENT_ID = 'testExperiment'
    static String TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING = 'testExperimentDisabled'

    @Primary
    @Bean
    MutableUserProvider mutableUserProvider() {
        return new MutableUserProvider()
    }

    @Primary
    @Bean
    InMemoryExperimentsRepository experimentsRepository() {
        return new InMemoryExperimentsRepository([
                new Experiment(
                        TEST_EXPERIMENT_ID,
                        [
                                new ExperimentVariant('base', []),
                                new ExperimentVariant('v1', [])
                        ],
                        'description',
                        'link',
                        'owner',
                        [],
                        true,
                        null,
                        null,
                        null
                ),
                new Experiment(
                        TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING,
                        [
                                new ExperimentVariant('base', []),
                                new ExperimentVariant('v1', [])
                        ],
                        'description',
                        'link',
                        'owner',
                        [],
                        false,
                        null,
                        null,
                        null
                )
        ])
    }
}

class MutableUserProvider implements UserProvider {
    User user

    def setUser(User user) {
        this.user = user
    }

    @Override
    User getCurrentUser() {
        return user;
    }
}

