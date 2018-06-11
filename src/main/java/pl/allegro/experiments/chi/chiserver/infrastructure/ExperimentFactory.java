package pl.allegro.experiments.chi.chiserver.infrastructure;

import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
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

    public Optional<ClientExperiment> clientExperimentFromGroupedExperiment(ExperimentDefinition experimentDefinition) {
        return experimentGroupRepository.findByExperimentId(experimentDefinition.getId())
                .map(experimentGroup -> {
                    int percentageRangeStart = 0;

                    for (String experimentId: experimentGroup.getExperiments()) {
                        if (!experimentId.equals(experimentDefinition.getId())) {
                            ExperimentDefinition currentExperiment = experimentsRepository.getExperiment(experimentId)
                                    .get()
                                    .getDefinition()
                                    .get();

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
                            for (String variantName: experimentDefinition.getVariantNames()) {
                                List<Predicate> variantPredicates = new ArrayList<>();
                                if (experimentDefinition.getInternalVariantName().isPresent()
                                        && experimentDefinition.getInternalVariantName().get().equals(variantName)) {
                                    variantPredicates.add(new InternalPredicate());
                                }

                                List<PercentageRange> ranges = new ArrayList<>();
                                if (variantName.equals("base")) {
                                    ranges.add(new PercentageRange(100 - experimentDefinition.getPercentage().get(), 100));
                                } else {
                                    ranges.add(new PercentageRange(percentageRangeStart, percentageRangeStart + experimentDefinition.getPercentage().get()));
                                    percentageRangeStart += experimentDefinition.getPercentage().get();
                                }
                                variantPredicates.add(new ShredHashRangePredicate(ranges, experimentGroup.getSalt()));
                                variants.add(new ExperimentVariant(variantName, variantPredicates));
                            }
                            return new ClientExperiment(
                                    experimentId,
                                    variants,
                                    experimentDefinition.isReportingEnabled(),
                                    experimentDefinition.getActivityPeriod(),
                                    experimentDefinition.getStatus()
                            );
                        }
                    }

                    return null;
                });
    }

    public AdminExperiment adminExperiment(Experiment experiment) {
        if (experimentGroupRepository.experimentInGroup(experiment.getId())) {
            return new AdminExperiment(
                    experiment.getDefinition().get(), // grouped experiment has definition
                    userProvider.getCurrentUser(),
                    clientExperimentFromGroupedExperiment(experiment.getDefinition().get()).get());
        } else {
            return experiment.getDefinition().map(it ->
                new AdminExperiment(it, userProvider.getCurrentUser(), new ClientExperiment(it.toExperiment()))
            ).orElse(new AdminExperiment(experiment));
        }
    }
}
