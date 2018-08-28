package pl.allegro.experiments.chi.chiserver.infrastructure;

import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.domain.User;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// todo refactor/optimize
public class ExperimentFactory {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;

    public ExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider) {
        this.experimentGroupRepository = experimentGroupRepository;
        this.experimentsRepository = experimentsRepository;
        this.userProvider = userProvider;
    }

    public ClientExperiment clientExperiment(ExperimentDefinition experiment) {
        return experimentGroupRepository.findByExperimentId(experiment.getId())
                .map(it -> clientExperimentFromGroupedExperiment(experiment, it))
                .orElseGet(() -> new ClientExperiment(experiment));
    }

    private ClientExperiment clientExperimentFromGroupedExperiment(ExperimentDefinition experiment, ExperimentGroup experimentGroup) {
        int percentageRangeStart = 0;
        for (String experimentId: experimentGroup.getExperiments()) {
            if (!experimentId.equals(experiment.getId())) {
                ExperimentDefinition currentExperiment = experimentsRepository.getExperiment(experimentId).get();
                if (currentExperiment.getStatus().equals(ExperimentStatus.DRAFT)) {
                    continue;
                }
                int currentExperimentPercentage = currentExperiment.getPercentage().get();
                long numberOfNonBaseVariants = currentExperiment.getVariantNames().stream()
                        .filter(v -> !v.equals("base"))
                        .count();
                percentageRangeStart += (int) numberOfNonBaseVariants * currentExperimentPercentage;
            } else {
                List<ExperimentVariant> variants = new ArrayList<>();
                for (String variantName: experiment.getVariantNames()) {
                    List<Predicate> variantPredicates = new ArrayList<>();
                    if (experiment.getInternalVariantName().isPresent()
                            && experiment.getInternalVariantName().get().equals(variantName)) {
                        variantPredicates.add(new InternalPredicate());
                    }

                    List<PercentageRange> ranges = new ArrayList<>();
                    if (variantName.equals("base")) {
                        ranges.add(new PercentageRange(100 - experiment.getPercentage().get(), 100));
                    } else {
                        ranges.add(new PercentageRange(percentageRangeStart, percentageRangeStart + experiment.getPercentage().get()));
                        percentageRangeStart += experiment.getPercentage().get();
                    }
                    variantPredicates.add(new ShredHashRangePredicate(ranges, experimentGroup.getSalt()));
                    variants.add(new ExperimentVariant(variantName, variantPredicates));
                }
                return new ClientExperiment(
                        experimentId,
                        variants,
                        experiment.getActivityPeriod(),
                        experiment.getStatus()
                );
            }
        }
        return null;
    }

    public AdminExperiment adminExperiment(ExperimentDefinition experiment) {
        return new AdminExperiment(experiment, userProvider.getCurrentUser(), clientExperiment(experiment));
    }
}
