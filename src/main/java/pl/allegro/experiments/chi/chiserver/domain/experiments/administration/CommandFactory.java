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

    Command createExperimentCommand(ExperimentCreationRequest request) {
        Preconditions.checkNotNull(request);
        CreateExperimentCommand experimentCommand = new CreateExperimentCommand(experimentsRepository, userProvider, request);
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command startExperimentCommand(
=======
    public StartExperimentCommand startExperimentCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
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
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command prolongExperimentCommand(
=======
    public ProlongExperimentCommand prolongExperimentCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
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
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command stopExperimentCommand(String experimentId) {
        StopExperimentCommand experimentCommand = new StopExperimentCommand(
=======
    public StopExperimentCommand stopExperimentCommand(String experimentId) {
        return new StopExperimentCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);

    }

<<<<<<< HEAD
    Command pauseExperimentCommand(String experimentId) {
        PauseExperimentCommand experimentCommand = new PauseExperimentCommand(
=======
    public PauseExperimentCommand pauseExperimentCommand(String experimentId) {
        return new PauseExperimentCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command resumeExperimentCommand(String experimentId) {
        ResumeExperimentCommand experimentCommand = new ResumeExperimentCommand(
=======
    public ResumeExperimentCommand resumeExperimentCommand(String experimentId) {
        return new ResumeExperimentCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command deleteExperimentCommand(String experimentId) {
=======
    public DeleteExperimentCommand deleteExperimentCommand(String experimentId) {
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
        Preconditions.checkNotNull(experimentId);
        DeleteExperimentCommand experimentCommand = new DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentId,
                statisticsRepository,
                experimentGroupRepository
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command updateDescriptionsCommand(String experimentId, UpdateExperimentProperties properties) {
=======
    public UpdateDescriptionsCommand updateDescriptionsCommand(String experimentId, UpdateExperimentProperties properties) {
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        UpdateDescriptionsCommand experimentCommand = new UpdateDescriptionsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command updateVariantsCommand(String experimentId, UpdateVariantsProperties properties) {
=======
    public UpdateVariantsCommand updateVariantsCommand(String experimentId, UpdateVariantsProperties properties) {
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        UpdateVariantsCommand experimentCommand = new UpdateVariantsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentGroupRepository
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command updateExperimentEventDefinitionsCommand(
=======
    public UpdateExperimentEventDefinitionsCommand updateExperimentEventDefinitionsCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
            String experimentId,
            List<EventDefinition> eventDefinitions) {
        UpdateExperimentEventDefinitionsCommand experimentCommand = new UpdateExperimentEventDefinitionsCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                eventDefinitions
        );
        return new NotificationDecoratorCommand(experimentCommand, notificator, userProvider);
    }

<<<<<<< HEAD
    Command createExperimentGroupCommand(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
=======
    public CreateExperimentGroupCommand createExperimentGroupCommand(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
        return new CreateExperimentGroupCommand(
                experimentGroupRepository,
                experimentsRepository,
                userProvider,
                experimentGroupCreationRequest,
                permissionsAwareExperimentRepository
        );
    }

<<<<<<< HEAD
    Command createPairedExperimentCommand(
=======
    public CreatePairedExperimentCommand createPairedExperimentCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
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

<<<<<<< HEAD
    Command makeExperimentFullOnCommand(
=======
    public MakeExperimentFullOnCommand makeExperimentFullOnCommand(
>>>>>>> CHIBOX-118 refactor command tests and add utility traits
            String experimentId,
            MakeExperimentFullOnProperties properties) {
        var command = new MakeExperimentFullOnCommand(
                experimentId,
                properties,
                experimentsRepository,
                experimentGroupRepository,
                permissionsAwareExperimentRepository
        );

        return new NotificationDecoratorCommand(command, notificator, userProvider);
    }
}
