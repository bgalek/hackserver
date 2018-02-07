package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.interactions.InMemoryInteractionRepository

@Configuration
class InteractionsIntegrationTestConfig {
    static String TEST_EXPERIMENT_ID = 'testExperiment'
    static String TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING = 'testExperimentDisabled'

    @Primary
    @Bean
    InMemoryInteractionRepository experimentInteractionRepository() {
        return new InMemoryInteractionRepository()
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
