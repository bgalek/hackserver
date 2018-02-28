package pl.allegro.experiments.chi.chiserver.application.administration

import pl.allegro.experiments.chi.chiserver.domain.User
import pl.allegro.experiments.chi.chiserver.domain.experiments.Experiment
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.create.CreateExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.pause.PauseExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.resume.ResumeExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentCommand
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.start.StartExperimentProperties
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.stop.StopExperimentCommand

import static pl.allegro.experiments.chi.chiserver.application.administration.CommandTestUtils.simpleExperimentRequest

trait PreparedExperiments {
    Experiment startedExperiment() {
        def experiment = draftExperiment()
        def properties = new StartExperimentProperties(30)
        startExperimentCommand(experiment.id, properties)
                .execute()
        return experimentsRepository.getExperiment(experiment.id)
    }

    Experiment endedExperiment() {
        def experiment = startedExperiment()
        stopExperimentCommand(experiment.id)
                .execute()
        return experimentsRepository.getExperiment(experiment.id)
    }

    Experiment pausedExperiment() {
        def experiment = startedExperiment()
        pauseCommand(experiment.id)
                .execute()
        return experimentsRepository.getExperiment(experiment.id)
    }

    StartExperimentCommand startExperimentCommand(String experimentId, StartExperimentProperties properties) {
        return new StartExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentGetter,
                experimentId)
    }

    StopExperimentCommand stopExperimentCommand(String experimentId) {
        return new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }

    Experiment draftExperiment() {
        def id = UUID.randomUUID().toString()
        mutableUserProvider.user = new User('Root', [], true)
        def command = new CreateExperimentCommand(
                experimentsRepository,
                mutableUserProvider,
                simpleExperimentRequest(id))
        command.execute()
        experimentsRepository.getExperiment(id)
    }

    PauseExperimentCommand pauseCommand(String experimentId) {
        new PauseExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }

    ResumeExperimentCommand resumeCommand(String experimentId) {
        new ResumeExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentGetter)
    }
}
