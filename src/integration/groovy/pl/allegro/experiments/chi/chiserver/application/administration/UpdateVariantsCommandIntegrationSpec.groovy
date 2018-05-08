package pl.allegro.experiments.chi.chiserver.application.administration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ContextConfiguration
import pl.allegro.experiments.chi.chiserver.BaseIntegrationSpec
import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.*
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentsTestConfig
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.MutableUserProvider

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

@ContextConfiguration(classes = [ExperimentsTestConfig])
@DirtiesContext
class UpdateVariantsCommandIntegrationSpec extends BaseIntegrationSpec {

    @Autowired
    ExperimentsRepository experimentsRepository

    @Autowired
    MutableUserProvider mutableUserProvider

    PermissionsAwareExperimentRepository permissionsAwareExperimentGetter

    def setup() {
        permissionsAwareExperimentGetter = new PermissionsAwareExperimentRepository(experimentsRepository, mutableUserProvider)
    }

    def "should change variants configuration"() {
        given:
        def experiment = experimentCreatedByRoot()

        and:
        def startCommand = new StartExperimentCommand(
                experimentsRepository,
                new StartExperimentProperties(30),
                permissionsAwareExperimentGetter,
                experiment.id)
        startCommand.execute()

        and:
        experiment = experimentsRepository.getExperiment(experiment.id).get()

        and:
        def updateVariantsCommand = new UpdateVariantsCommand(
                experiment.id,
                new UpdateVariantsProperties(['va', 'vb'], 'iname', 13, 'phone'),
                experimentsRepository,
                permissionsAwareExperimentGetter)

        when:
        updateVariantsCommand.execute()

        then:
        def updated = experimentsRepository.getExperiment(experiment.id).get()
        updated.definition.get().deviceClass.name() == 'phone'
        updated.definition.get().internalVariantName.get() == 'iname'
        updated.definition.get().variantNames == ['va', 'vb']
        updated.definition.get().percentage.get() == 13
    }

    Experiment experimentCreatedByRoot() {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(experimentsRepository, mutableUserProvider, simpleExperimentRequest(id))
        command.execute()
        return experimentsRepository.getExperiment(id).get()
    }
}