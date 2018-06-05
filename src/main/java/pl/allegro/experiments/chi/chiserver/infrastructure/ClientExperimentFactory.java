package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.common.base.Preconditions;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// todo refactor/optimize
public class ClientExperimentFactory {
    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentsRepository experimentsRepository;

    public ClientExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository) {
        this.experimentGroupRepository = experimentGroupRepository;
        this.experimentsRepository = experimentsRepository;
    }

    public Optional<ClientExperiment> fromGroupedExperiment(ExperimentDefinition assignableExperiment) {
        Preconditions.checkArgument(assignableExperiment.isAssignable());

        return experimentGroupRepository.findByExperimentId(assignableExperiment.getId())
                .map(experimentGroup -> {
                    int percentageRangeStart = 0;

                    for (String experimentId: experimentGroup.getExperiments()) {
                        if (!experimentId.equals(assignableExperiment.getId())) {
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
                            for (String variantName: assignableExperiment.getVariantNames()) {
                                List<Predicate> variantPredicates = new ArrayList<>();
                                if (assignableExperiment.getInternalVariantName().isPresent()
                                        && assignableExperiment.getInternalVariantName().get().equals(variantName)) {
                                    variantPredicates.add(new InternalPredicate());
                                }

                                List<PercentageRange> ranges = new ArrayList<>();
                                if (variantName.equals("base")) {
                                    ranges.add(new PercentageRange(100 - assignableExperiment.getPercentage().get(), 100));
                                } else {
                                    ranges.add(new PercentageRange(percentageRangeStart, percentageRangeStart + assignableExperiment.getPercentage().get()));
                                    percentageRangeStart += assignableExperiment.getPercentage().get();
                                }
                                variantPredicates.add(new ShredHashRangePredicate(ranges, experimentGroup.getSalt()));
                                variants.add(new ExperimentVariant(variantName, variantPredicates));
                            }
                            return new ClientExperiment(
                                    experimentId,
                                    variants,
                                    assignableExperiment.isReportingEnabled(),
                                    assignableExperiment.getActivityPeriod(),
                                    assignableExperiment.getStatus()
                            );
                        }
                    }

                    return null;
                });
    }
}
