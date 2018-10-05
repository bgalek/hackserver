package pl.allegro.experiments.chi.chiserver.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.allegro.experiments.chi.chiserver.application.experiments.AdminExperiment;
import pl.allegro.experiments.chi.chiserver.domain.UserProvider;
import pl.allegro.experiments.chi.chiserver.domain.experiments.*;
import pl.allegro.experiments.chi.chiserver.domain.experiments.client.ClientExperiment;
import pl.allegro.experiments.chi.chiserver.domain.experiments.client.ClientExperimentRenderer;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroup;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ExperimentGroupRepository;
import pl.allegro.experiments.chi.chiserver.domain.experiments.groups.ShredHashRangePredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientExperimentFactory {
    private static final Logger logger = LoggerFactory.getLogger(ClientExperimentFactory.class);

    private final ExperimentGroupRepository experimentGroupRepository;
    private final ExperimentsRepository experimentsRepository;
    private final UserProvider userProvider;

    @Autowired
    public ClientExperimentFactory(
            ExperimentGroupRepository experimentGroupRepository,
            ExperimentsRepository experimentsRepository,
            UserProvider userProvider) {
        this.experimentGroupRepository = experimentGroupRepository;
        this.experimentsRepository = experimentsRepository;
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

    /**
     *
     * remove after migration
     */
    @Deprecated
    public void persistAllocationForLegacyGroup(ExperimentGroup group) {
        logger.info("persisting AlloctionTable for group " + group.getId() );

        List<ClientExperiment> ces = group.getExperiments().stream()
                .map(expId -> experimentsRepository.getExperiment(expId).get())
                .map(e -> clientExperimentFromGroupedExperiment(e, group))
                .filter(e -> !e.getStatus().equals(ExperimentStatus.DRAFT))
                .collect(Collectors.toList());


        ExperimentGroup mutatedGroup = group;
        for (ClientExperiment ce : ces) {
            logger.info(".. creating records for experiment " + ce.getId() + ", " + ce.getStatus() );

            List<VariantPercentageAllocation> currentAllocation = new ArrayList<>();
            for (ExperimentVariant ev : ce.getVariants()) {
                ev.getPredicates().stream()
                        .filter(p -> p instanceof ShredHashRangePredicate)
                        .map(sh -> new VariantPercentageAllocation(ev.getName(), ((ShredHashRangePredicate) sh).getRanges().get(0)))
                        .forEach(it -> {
                            logger.info(".. .. record: "+ it.getVariantName() + " "+ it.getRange());
                            currentAllocation.add(it);
                        });
            }

            mutatedGroup = mutatedGroup.allocateExistingExperimentLegacy(ce.getId(), currentAllocation);
            List<ExperimentDefinition> draftsToRemoveFromGroup = group.getExperiments().stream()
                    .map(expId -> experimentsRepository.getExperiment(expId).get())
                    .filter(ExperimentDefinition::isDraft)
                    .collect(Collectors.toList());
            for (ExperimentDefinition e: draftsToRemoveFromGroup) {
                mutatedGroup = mutatedGroup.removeExperiment(e.getId());
            }
            experimentGroupRepository.save(mutatedGroup);

        }
    }

    /**
     * legacy on-the-fly renderer, remove it after migration
     */
    @Deprecated
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

                if (experiment.getInternalVariantName().isPresent()) {
                    List<Predicate> internalVariantPredicates = new ArrayList<>(List.of(new InternalPredicate()));
                    addDeviceClassPredicateIfFound(internalVariantPredicates, experiment);
                    addCustomParameterPredicateIfFound(internalVariantPredicates, experiment);
                    variants.add(new ExperimentVariant(experiment.getInternalVariantName().get(), internalVariantPredicates));
                }

                for (String variantName: experiment.getVariantNames()) {
                    List<Predicate> variantPredicates = new ArrayList<>();
                    addDeviceClassPredicateIfFound(variantPredicates, experiment);
                    addCustomParameterPredicateIfFound(variantPredicates, experiment);

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

    private void addDeviceClassPredicateIfFound(List<Predicate> predicates, ExperimentDefinition experiment) {
        experiment.getDeviceClass().ifPresent(d -> predicates.add(new DeviceClassPredicate(d.toJsonString())));
    }

    private void addCustomParameterPredicateIfFound(List<Predicate> predicates, ExperimentDefinition experiment) {
        experiment.getCustomParameter().ifPresent(p -> predicates.add(new CustomParameterPredicate(p.getName(), p.getValue())));
    }
}
