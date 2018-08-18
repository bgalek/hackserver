package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.notifications.Notificator;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.statistics.classic.StatisticsRepository;

import java.util.List;

@Service
public class CommandFactory {
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;
    private final PermissionsAwareExperimentRepository permissionsAwareExperimentRepository;
    private final StatisticsRepository statisticsRepository;
    private final ExperimentGroupRepository experimentGroupRepository;
    private final Notificator notificator;

    @Autowired
    public CommandFactory(ExperimentsRepository experimentsRepository,
                          UserProvider userProvider,
                          PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
                          StatisticsRepository statisticsRepository,
                          ExperimentGroupRepository experimentGroupRepository,
                          Notificator emailService) {
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.statisticsRepository = statisticsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
        this.notificator = emailService;
    }

    NotificationAwareCommand createExperimentCommand(ExperimentCreationRequest request) {
        Preconditions.checkNotNull(request);
        CreateExperimentCommand experimentCommand = new CreateExperimentCommand(experimentsRepository, userProvider, request);
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand startExperimentCommand(
            String experimentId,
            StartExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);
        StartExperimentCommand experimentCommand = new StartExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentRepository,
                experimentGroupRepository,
                experimentId
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand prolongExperimentCommand(
            String experimentId,
            ProlongExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);
        ProlongExperimentCommand experimentCommand = new ProlongExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentRepository,
                experimentId
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand stopExperimentCommand(String experimentId) {
        StopExperimentCommand experimentCommand = new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);

    }

    NotificationAwareCommand pauseExperimentCommand(String experimentId) {
        PauseExperimentCommand experimentCommand = new PauseExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand resumeExperimentCommand(String experimentId) {
        ResumeExperimentCommand experimentCommand = new ResumeExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand deleteExperimentCommand(String experimentId) {
        Preconditions.checkNotNull(experimentId);
        DeleteExperimentCommand experimentCommand = new DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentId,
                statisticsRepository,
                experimentGroupRepository
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand updateDescriptionsCommand(String experimentId, UpdateExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        UpdateDescriptionsCommand experimentCommand = new UpdateDescriptionsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand updateVariantsCommand(String experimentId, UpdateVariantsProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        UpdateVariantsCommand experimentCommand = new UpdateVariantsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentGroupRepository
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    NotificationAwareCommand updateExperimentEventDefinitionsCommand(
            String experimentId,
            List<EventDefinition> eventDefinitions) {
        UpdateExperimentEventDefinitionsCommand experimentCommand = new UpdateExperimentEventDefinitionsCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                eventDefinitions
        );
        return new NotificationAwareCommand(experimentCommand, notificator, userProvider);
    }

    CreateExperimentGroupCommand createExperimentGroupCommand(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        return new CreateExperimentGroupCommand(
                experimentGroupRepository,
                experimentsRepository,
                userProvider,
                experimentGroupCreationRequest,
                permissionsAwareExperimentRepository
        );
    }

    CreatePairedExperimentCommand createPairedExperimentCommand(
            PairedExperimentCreationRequest pairedExperimentCreationRequest) {
        return new CreatePairedExperimentCommand(
                new CreateExperimentCommand(
                        experimentsRepository,
                        userProvider,
                        pairedExperimentCreationRequest.getExperimentCreationRequest()),
                new CreateExperimentGroupCommand(
                        experimentGroupRepository,
                        experimentsRepository,
                        userProvider,
                        pairedExperimentCreationRequest.getExperimentGroupCreationRequest(),
                        permissionsAwareExperimentRepository),
                new DeleteExperimentCommand(
                        experimentsRepository,
                        permissionsAwareExperimentRepository,
                        pairedExperimentCreationRequest.getExperimentCreationRequest().getId(),
                        statisticsRepository,
                        experimentGroupRepository));
    }

    NotificationAwareCommand makeExperimentFullOnCommand(
            String experimentId,
            MakeExperimentFullOnProperties properties) {
        var command = new MakeExperimentFullOnCommand(
                experimentId,
                properties,
                experimentsRepository,
                experimentGroupRepository,
                permissionsAwareExperimentRepository
        );

        return new NotificationAwareCommand(command, notificator, userProvider);
    }
}
