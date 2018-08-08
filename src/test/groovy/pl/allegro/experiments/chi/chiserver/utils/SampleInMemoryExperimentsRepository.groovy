package pl.allegro.experiments.chi.chiserver.utils

import groovy.transform.CompileStatic
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentStatus
import pl.allegro.experiments.chi.chiserver.infrastructure.InMemoryExperimentsRepository

@CompileStatic
class SampleInMemoryExperimentsRepository {
    static String TEST_EXPERIMENT_ID = 'testExperiment'
    static String FULL_ON_TEST_EXPERIMENT_ID = 'fullOnTestExperiment'

    static InMemoryExperimentsRepository createSampleRepository() {
        Experiment experiment = ExperimentFactory.experimentWithId(TEST_EXPERIMENT_ID)
                .mutate()
                .definition(ExperimentFactory.backendReportingDefinition(TEST_EXPERIMENT_ID))
                .build()
        Experiment fullOnExperiment = ExperimentFactory.experimentWithId(FULL_ON_TEST_EXPERIMENT_ID)
                .mutate()
                .definition(ExperimentFactory.backendReportingDefinition(FULL_ON_TEST_EXPERIMENT_ID))
                .build()
                .getDefinition()
                .get()
                .start(30)
                .makeFullOn('v2')
                .toExperiment()
        return new InMemoryExperimentsRepository([
                experiment,
                fullOnExperiment
        ])
    }
}
