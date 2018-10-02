package pl.allegro.experiments.chi.chiserver.domain.experiments.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class ClientExperimentRenderer {

    private final ExperimentDefinition experiment;
    private final ExperimentGroup group;

    public ClientExperimentRenderer(ExperimentDefinition experiment, ExperimentGroup group) {
        Preconditions.checkArgument(experiment != null);
        this.experiment = experiment;
        this.group = group;
    }

    public ClientExperiment render() {
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
            return experiment.renderRegularVariantsSolo().stream()
                    .map(a -> renderVariantWithTopLevelPredicates(a.getVariantName(), new HashRangePredicate(a.getRange()))).collect(toList());
        }
        return renderRegularVariantsInGroup();
    }

    private List<ExperimentVariant> renderRegularVariantsInGroup() {
        if (!experiment.hasAnyPercentagePredicate()) {
            return Collections.emptyList();
        }
        return experiment.getVariantNames().stream()
                .map(vName -> {
                    List<PercentageRange> ranges = group.getAllocationFor(experiment.getId(), experiment.getPercentage().get(), vName);
                    ShredHashRangePredicate mainPredicate = new ShredHashRangePredicate(ranges, group.getSalt());
                    return renderVariantWithTopLevelPredicates(vName, mainPredicate);
                })
                .collect(toList());
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
}
