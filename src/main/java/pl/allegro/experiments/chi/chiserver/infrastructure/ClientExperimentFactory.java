package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinition;
import pl.allegro.experiments.chi.chiserver.domain.experiments.client.ClientExperiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.client.ClientExperimentRenderer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;

import java.util.Optional;

@Service
public class ClientExperimentFactory {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final UserProvider userProvider;

    @Autowired
    public ClientExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            UserProvider userProvider) {
        this.experimentGroupRepository = experimentGroupRepository;
        this.userProvider = userProvider;
    }

    public ClientExperiment clientExperiment(ExperimentDefinition experiment) {
        ClientExperimentRenderer renderer = new ClientExperimentRenderer(experiment, getGroup(experiment).orElse(null));
        return renderer.render();
    }

    public AdminExperiment adminExperiment(ExperimentDefinition experiment) {
        int maxPossibleAllocation = getGroup(experiment)
                .map(g -> g.getMaxPossibleScaleUp(experiment))
                .orElse(experiment.getMaxPossibleScaleUp());

        return new AdminExperiment(experiment, userProvider.getCurrentUser(), clientExperiment(experiment), maxPossibleAllocation);
    }

    private Optional<ExperimentGroup> getGroup(ExperimentDefinition experiment) {
        return experimentGroupRepository.findByExperimentId(experiment.getId());
    }
}
