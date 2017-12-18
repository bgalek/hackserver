package pl.allegro.experiments.chi.chiserver.interactions

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import pl.allegro.experiments.chi.chiserver.domain.Experiment
import pl.allegro.experiments.chi.chiserver.domain.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.domain.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.interactions.infrastructure.InMemoryInteractionRepository

import java.time.Instant
import java.time.ZoneId

@Configuration
class InteractionsIntegrationTestConfig {
    static String TEST_EXPERIMENT_ID = 'testExperiment'
    static String TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING = 'testExperimentDisabled'

    @Primary
    @Bean
    InMemoryInteractionRepository experimentInteractionRepository(ExperimentsRepository experimentsRepository) {
        return new InMemoryInteractionRepository(experimentsRepository)
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
                        true,
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
                        false,
                        null,
                        null
                )
        ])
    }
}
