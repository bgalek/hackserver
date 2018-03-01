package pl.allegro.experiments.chi.chiserver.utils

import groovy.transform.CompileStatic
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentVariant
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository
import pl.allegro.experiments.chi.chiserver.utils.ExperimentFactory

@CompileStatic
class SampleInMemoryExperimentsRepository {
    static String TEST_EXPERIMENT_ID = 'testExperiment'
    static String TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING = 'testExperimentDisabled'

    static InMemoryExperimentsRepository createSampleRepository() {
        Experiment experiment = ExperimentFactory.experimentWithId(TEST_EXPERIMENT_ID)
                .mutate()
                .reportingEnabled(true)
                .build()
        Experiment experimentWithoutReporting = experiment.mutate()
                .id(TEST_EXPERIMENT_ID_WITH_DISABLED_REPORTING)
                .reportingEnabled(false)
                .build()
        return new InMemoryExperimentsRepository([
                experiment,
                experimentWithoutReporting
        ])
    }
}
