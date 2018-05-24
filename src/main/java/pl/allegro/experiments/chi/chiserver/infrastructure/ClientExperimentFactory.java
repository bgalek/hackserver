package pl.allegro.experiments.chi.chiserver.infrastructure;

import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClientExperimentFactory {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentsRepository experimentsRepository;

    public ClientExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository) {
        this.experimentGroupRepository = experimentGroupRepository;
        this.experimentsRepository = experimentsRepository;
    }

    public Optional<ClientExperiment> fromGroupedExperiment(ExperimentDefinition experimentDefinition) {
        // is assignable
        return experimentGroupRepository.getExperimentGroup(experimentDefinition.getId())
                .map(experimentGroup -> {
                    int percentageRangeStart = 0;
                    int maxBasePercentage = -1;

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
                            maxBasePercentage = Integer.max(maxBasePercentage, currentExperimentPercentage);
                            percentageRangeStart = (int) numberOfNonBaseVariants * currentExperimentPercentage;
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
                                    maxBasePercentage = Integer.max(maxBasePercentage, experimentDefinition.getPercentage().get());
                                    ranges.add(new PercentageRange(100 - maxBasePercentage, 100));
                                } else {
                                    ranges.add(new PercentageRange(percentageRangeStart, percentageRangeStart + experimentDefinition.getPercentage().get()));
                                    percentageRangeStart += experimentDefinition.getPercentage().get();
                                }
                                variantPredicates.add(new ShredHashRangePredicate(ranges, experimentGroup.getNameSpace()));
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
}
