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
    private final Notificator emailService;

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
        this.emailService = emailService;
    }

    BasicExperimentCommand createExperimentCommand(ExperimentCreationRequest request) {
        Preconditions.checkNotNull(request);
        CreateExperimentCommand experimentCommand = new CreateExperimentCommand(experimentsRepository, userProvider, request);
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand startExperimentCommand(
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
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand prolongExperimentCommand(
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
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand stopExperimentCommand(String experimentId) {
        StopExperimentCommand experimentCommand = new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);

    }

    BasicExperimentCommand pauseExperimentCommand(String experimentId) {
        PauseExperimentCommand experimentCommand = new PauseExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand resumeExperimentCommand(String experimentId) {
        ResumeExperimentCommand experimentCommand = new ResumeExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand deleteExperimentCommand(String experimentId) {
        Preconditions.checkNotNull(experimentId);
        DeleteExperimentCommand experimentCommand = new DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentId,
                statisticsRepository,
                experimentGroupRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand updateDescriptionsCommand(String experimentId, UpdateExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        UpdateDescriptionsCommand experimentCommand = new UpdateDescriptionsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand updateVariantsCommand(String experimentId, UpdateVariantsProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        UpdateVariantsCommand experimentCommand = new UpdateVariantsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentGroupRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand updateExperimentEventDefinitionsCommand(
            String experimentId,
            List<EventDefinition> eventDefinitions) {
        UpdateExperimentEventDefinitionsCommand experimentCommand = new UpdateExperimentEventDefinitionsCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                eventDefinitions
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand createExperimentGroupCommand(ExperimentGroupCreationRequest experimentGroupCreationRequest) {
        CreateExperimentGroupCommand experimentCommand = new CreateExperimentGroupCommand(
                experimentGroupRepository,
                experimentsRepository,
                userProvider,
                experimentGroupCreationRequest,
                permissionsAwareExperimentRepository
        );
        return new BasicExperimentCommand(experimentCommand, emailService, userProvider);
    }

    BasicExperimentCommand createPairedExperimentCommand(
            PairedExperimentCreationRequest pairedExperimentCreationRequest) {
        CreatePairedExperimentCommand createPairedExperimentCommand = new CreatePairedExperimentCommand(
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
        return new BasicExperimentCommand(createPairedExperimentCommand, emailService, userProvider);
    }

    ExperimentCommand makeExperimentFullOnCommand(
            String experimentId,
            MakeExperimentFullOnProperties properties) {
        return new MakeExperimentFullOnCommand(
                experimentId,
                properties,
                experimentsRepository,
                experimentGroupRepository,
                permissionsAwareExperimentRepository
        );
    }
}
