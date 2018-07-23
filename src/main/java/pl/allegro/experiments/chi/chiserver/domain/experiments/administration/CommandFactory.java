package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.EventDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentsRepository;
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

    @Autowired
    public CommandFactory(ExperimentsRepository experimentsRepository,
                          UserProvider userProvider,
                          PermissionsAwareExperimentRepository permissionsAwareExperimentRepository,
                          StatisticsRepository statisticsRepository,
                          ExperimentGroupRepository experimentGroupRepository) {
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
        this.permissionsAwareExperimentRepository = permissionsAwareExperimentRepository;
        this.statisticsRepository = statisticsRepository;
        this.experimentGroupRepository = experimentGroupRepository;
    }

    CreateExperimentCommand createExperimentCommand(ExperimentCreationRequest request) {
        Preconditions.checkNotNull(request);
        return new CreateExperimentCommand(experimentsRepository, userProvider, request);
    }

    StartExperimentCommand startExperimentCommand(
            String experimentId,
            StartExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);
        return new StartExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentRepository,
                experimentGroupRepository,
                experimentId
        );
    }

    ProlongExperimentCommand prolongExperimentCommand(
            String experimentId,
            ProlongExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);
        return new ProlongExperimentCommand(
                experimentsRepository,
                properties,
                permissionsAwareExperimentRepository,
                experimentId
        );
    }

    StopExperimentCommand stopExperimentCommand(String experimentId) {
        return new StopExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
    }

    PauseExperimentCommand pauseExperimentCommand(String experimentId) {
        return new PauseExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
    }

    ResumeExperimentCommand resumeExperimentCommand(String experimentId) {
        return new ResumeExperimentCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
    }

    DeleteExperimentCommand deleteExperimentCommand(String experimentId) {
        Preconditions.checkNotNull(experimentId);
        return new DeleteExperimentCommand(
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentId,
                statisticsRepository,
                experimentGroupRepository
        );
    }

    UpdateDescriptionsCommand updateDescriptionsCommand(String experimentId, UpdateExperimentProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        return new UpdateDescriptionsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository
        );
    }

    UpdateVariantsCommand updateVariantsCommand(String experimentId, UpdateVariantsProperties properties) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(properties);

        return new UpdateVariantsCommand(
                experimentId,
                properties,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                experimentGroupRepository
        );
    }

    UpdateExperimentEventDefinitionsCommand updateExperimentEventDefinitionsCommand(
            String experimentId,
            List<EventDefinition> eventDefinitions) {
        return new UpdateExperimentEventDefinitionsCommand(
                experimentId,
                experimentsRepository,
                permissionsAwareExperimentRepository,
                eventDefinitions
        );
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
                createExperimentCommand(pairedExperimentCreationRequest.getExperimentCreationRequest()),
                createExperimentGroupCommand(pairedExperimentCreationRequest.getExperimentGroupCreationRequest()),
                deleteExperimentCommand(pairedExperimentCreationRequest.getExperimentCreationRequest().getId())
        );

    }
}
