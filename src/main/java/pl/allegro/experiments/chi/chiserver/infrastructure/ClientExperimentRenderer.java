package pl.allegro.experiments.chi.chiserver.infrastructure;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentDefinitionException;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

class ClientExperimentRenderer {

    private final ExperimentDefinition experiment;
    private final ExperimentGroup group;

    ClientExperimentRenderer(ExperimentDefinition experiment, ExperimentGroup group) {
        Preconditions.checkArgument(experiment != null);
        this.experiment = experiment;
        this.group = group;
    }

    ClientExperiment render() {
        System.out.println("rendering experiment = " + experiment.getId() +", group: " + group);

        return new ClientExperiment(
                experiment.getId(),
                renderVariants(),
                experiment.getActivityPeriod(),
                experiment.getStatus()
        );
    }

    private List<ExperimentVariant> renderVariants() {
        if (experiment.isFullOn()) {
            return ImmutableList.of(renderFullOnVariant());
        }

        List<ExperimentVariant> variants = new ArrayList<>();

        experiment.getInternalVariantName().ifPresent(v -> variants.add(renderInternalVariant()));
        variants.addAll(renderRegularVariants());

        return variants;
    }

    private List<ExperimentVariant> renderRegularVariants() {
        if (group == null) {
            return renderRegularVariantsSolo();
        }
        return renderRegularVariantsInGroup();
    }

    private List<ExperimentVariant> renderRegularVariantsInGroup() {
        return experiment.getVariantNames().stream()
                .map(v -> {
                    List<PercentageRange> ranges = group.getAllocationFor(experiment.getId(), v);
                    return mapTpExperimentVariant(ranges, v);
                })
                .collect(toList());
    }

    private List<ExperimentVariant> renderRegularVariantsSolo() {
        if (!experiment.getPercentage().isPresent() || experiment.getVariantNames().isEmpty()) {
            return Collections.emptyList();
        }

        int percentage = experiment.getPercentage().get();

        final int maxPercentageVariant = 100 / experiment.getVariantNames().size();
        if (percentage > maxPercentageVariant) {
            throw new ExperimentDefinitionException(String.format("Percentage exceeds maximum value (%s > %s)", percentage, maxPercentageVariant));
        }

        return IntStream.range(0, experiment.getVariantNames().size())
                .mapToObj(i -> {
                    Predicate hash = convertToHashRangePredicateByIndex(i, maxPercentageVariant, experiment.getPercentage().get());
                    return renderVariantWithTopLevelPredicates(experiment.getVariantNames().get(i), hash);
                })
                .collect(toList());
    }

    private ExperimentVariant mapTpExperimentVariant(List<PercentageRange> ranges, String variantName) {
        //TODO shared base TRIM!
        ShredHashRangePredicate mainPredicate = new ShredHashRangePredicate(ranges, group.getSalt());
        return renderVariantWithTopLevelPredicates(variantName, mainPredicate);
    }

    private ExperimentVariant renderFullOnVariant() {
        return renderVariantWithTopLevelPredicates(experiment.getFullOnVariantName().get(), new FullOnPredicate());
    }

    private ExperimentVariant renderInternalVariant() {
        return renderVariantWithTopLevelPredicates(experiment.getInternalVariantName().get(), new InternalPredicate());
    }

    private ExperimentVariant renderVariantWithTopLevelPredicates(String variantName, Predicate mainPredicate) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(mainPredicate);

        //top-level predicates
        experiment.getDeviceClass().ifPresent(device -> predicates.add(new DeviceClassPredicate(device.toJsonString())));
        experiment.getCustomParameter().ifPresent(p -> predicates.add(new CustomParameterPredicate(p.getName(), p.getValue())));

        return new ExperimentVariant(variantName, predicates);
    }

    private Predicate convertToHashRangePredicateByIndex(int i, int maxPercentageVariant, int percentage) {
        return new HashRangePredicate(new PercentageRange(i * maxPercentageVariant,
                i * maxPercentageVariant + percentage));
    }
}
