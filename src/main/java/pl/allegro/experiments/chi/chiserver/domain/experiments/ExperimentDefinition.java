package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentDefinitionException;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static pl.allegro.experiments.chi.chiserver.domain.experiments.ExperimentDefinitionBuilder.experimentDefinition;

@Document(collection = "experimentDefinitions")
@TypeName("Experiment")
public class ExperimentDefinition {
    @org.springframework.data.annotation.Id
    private final String id;
    private final List<String> variantNames;
    private final String internalVariantName;
    private final String fullOnVariantName;
    private final Integer percentage;
    private final DeviceClass deviceClass;
    private final String description;
    private final String documentLink;
    private final String author;
    private final List<String> groups;
    private final ActivityPeriod activityPeriod;
    private final ExperimentStatus explicitStatus;
    @Transient
    private final ExperimentStatus status;
    private final ReportingDefinition reportingDefinition;
    private final CustomParameter customParameter;
    private final ZonedDateTime lastExplicitStatusChange;

    ExperimentDefinition(
            String id,
            List<String> variantNames,
            String internalVariantName,
            String fullOnVariantName,
            Integer percentage,
            DeviceClass deviceClass,
            String description,
            String documentLink,
            String author,
            List<String> groups,
            ActivityPeriod activityPeriod,
            ExperimentStatus explicitStatus,
            ReportingDefinition reportingDefinition,
            CustomParameter customParameter,
            ZonedDateTime lastExplicitStatusChange) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(variantNames);
        Preconditions.checkNotNull(reportingDefinition);
        Preconditions.checkNotNull(groups);
        Preconditions.checkArgument(internalVariantName == null || !internalVariantName.isEmpty());
        Preconditions.checkArgument(fullOnVariantName == null || !fullOnVariantName.isEmpty());
        this.id = id;
        this.variantNames = ImmutableList.copyOf(variantNames);
        this.internalVariantName = internalVariantName;
        this.fullOnVariantName = fullOnVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass != null ? deviceClass : DeviceClass.all;
        this.description = emptyToNull(description);
        this.documentLink = emptyToNull(documentLink);
        this.author = author;
        this.groups = ImmutableList.copyOf(groups);
        this.activityPeriod = activityPeriod;
        this.explicitStatus = explicitStatus;
        this.status = ExperimentStatus.of(explicitStatus, activityPeriod);
        this.reportingDefinition = reportingDefinition;
        this.customParameter = customParameter;
        this.lastExplicitStatusChange = lastExplicitStatusChange;
    }

    @Id
    @DiffInclude
    public String getId() {
        return id;
    }

    @DiffInclude
    public List<String> getVariantNames() {
        return variantNames;
    }

    @DiffInclude
    public ReportingDefinition getReportingDefinition() {
        return reportingDefinition.withImplicitEventDefinitionsIfGtm(this);
    }

    public ReportingDefinition getReportingDefinitionToSave() {
        return reportingDefinition;
    }

    @DiffInclude
    public Optional<String> getInternalVariantName() { return Optional.ofNullable(internalVariantName); }

    @DiffInclude
    public Optional<String> getFullOnVariantName() { return Optional.ofNullable(fullOnVariantName); }

    @DiffInclude
    public Optional<Integer> getPercentage() {
        return Optional.ofNullable(percentage);
    }

    public DeviceClass getDeviceClass() { return deviceClass; }

    @DiffInclude
    @PropertyName("deviceClass")
    Optional<String> getDeviceClassLegacyFormat() { return Optional.ofNullable(deviceClass.toJsonString()); }

    @DiffInclude
    public String getDescription() {
        return description;
    }

    @DiffInclude
    public String getDocumentLink() {
        return documentLink;
    }

    @DiffInclude
    public String getAuthor() {
        return author;
    }

    @DiffInclude
    public List<String> getGroups() {
        return groups;
    }

    public ActivityPeriod getActivityPeriod() {
        return activityPeriod;
    }

    @DiffInclude
    public CustomParameter getCustomParameter() {
        return customParameter;
    }

    public boolean hasCustomParam() {
        return customParameter != null;
    }

    @DiffInclude
    public LocalDateTime getActiveFrom() {
        if (activityPeriod == null) {
            return null;
        }
        return activityPeriod.getActiveFrom().toLocalDateTime();
    }

    @DiffInclude
    public LocalDateTime getActiveTo() {
        if (activityPeriod == null) {
            return null;
        }
        return activityPeriod.getActiveTo().toLocalDateTime();
    }

    @DiffInclude
    public ExperimentStatus getStatus() {
        return status;
    }

    public ZonedDateTime getLastExplicitStatusChange() {
        return lastExplicitStatusChange;
    }

    public Set<String> allVariantNames() {
        var allVariants = new HashSet<>(variantNames);
        if (internalVariantName != null) {
            allVariants.add(internalVariantName);
        }
        return Collections.unmodifiableSet(allVariants);
    }

    public boolean isDraft() {
        return getStatus() == ExperimentStatus.DRAFT;
    }

    public boolean isEnded() {
        return getStatus() == ExperimentStatus.ENDED;
    }

    public boolean isPaused() {
        return getStatus() == ExperimentStatus.PAUSED;
    }

    public boolean isActive() {
        return getStatus() == ExperimentStatus.ACTIVE;
    }

    public boolean isFullOn() {
        return getStatus() == ExperimentStatus.FULL_ON;
    }

    public boolean isEffectivelyEnded() {
        return isFullOn() || isEnded();
    }

    public boolean isEndable() {
        return isActive() || isFullOn();
    }

    public boolean isAssignable() {
        return !isEnded() && !isPaused();
    }

    public boolean shouldSaveInteractions() {
        return !isFullOn();
    }

    public ExperimentDefinition start(long experimentDurationDays) {
        final var from = ZonedDateTime.now();
        final var to = from.plusDays(experimentDurationDays);
        final var newActivityPeriod = new ActivityPeriod(from, to);
        return mutate()
                .activityPeriod(newActivityPeriod)
                .build();
    }

    public ExperimentDefinition stop() {
        if (isActive()) {
            return mutate().activityPeriod(this.activityPeriod.endNow()).build();
        }

        if (isFullOn()) {
            return mutate().changeExplicitStatus(null).build();
        }

        throw new IllegalStateException("illegal stop attempt");
    }

    public ExperimentDefinition pause() {
        return mutate()
                .changeExplicitStatus(ExperimentStatus.PAUSED)
                .build();
    }

    public ExperimentDefinition resume() {
        return mutate()
                .changeExplicitStatus(null)
                .build();
    }

    public ExperimentDefinition makeFullOn(String fullOnVariantName) {
        Preconditions.checkNotNull(fullOnVariantName);
        Preconditions.checkNotNull(this.activityPeriod);
        final ActivityPeriod activityPeriod = this.activityPeriod.endNow();
        return mutate()
                .activityPeriod(activityPeriod)
                .changeExplicitStatus(ExperimentStatus.FULL_ON)
                .fullOnVariantName(fullOnVariantName)
                .percentage(0)
                .build();
    }

    public ExperimentDefinition updateReportingDefinition(ReportingDefinition reportingDefinition) {
        Preconditions.checkArgument(reportingDefinition != null);
        return mutate().reportingDefinition(reportingDefinition).build();
    }

    public ExperimentDefinition prolong(long experimentAdditionalDays) {
        final var from = activityPeriod.getActiveFrom();
        final var to = activityPeriod.getActiveTo().plusDays(experimentAdditionalDays);
        final var newActivityPeriod = new ActivityPeriod(from, to);
        return mutate()
                .activityPeriod(newActivityPeriod)
                .build();
    }

    private ExperimentVariant convertVariantByIndex(int i, int maxPercentageVariant) {
        return convertVariant(
                variantNames.get(i),
                i * maxPercentageVariant,
                i * maxPercentageVariant + percentage);
    }

    private ExperimentVariant convertVariant(String variantName, int from, int to) {
        final var predicates = new ArrayList<Predicate>();
        predicates.add(new HashRangePredicate(new PercentageRange(from, to)));
        if (DeviceClass.all != deviceClass) {
            predicates.add(new DeviceClassPredicate(deviceClass.toJsonString()));
        }
        if (hasCustomParam()) {
            predicates.add(new CustomParameterPredicate(customParameter.getName(), customParameter.getValue()));
        }
        return new ExperimentVariant(variantName, predicates);
    }

    public ExperimentDefinitionBuilder mutate() {
        return  experimentDefinition()
                    .id(id)
                    .variantNames(variantNames)
                    .internalVariantName(internalVariantName)
                    .fullOnVariantName(fullOnVariantName)
                    .deviceClass(deviceClass)
                    .percentage(percentage)
                    .description(description)
                    .documentLink(documentLink)
                    .author(author)
                    .groups(groups)
                    .activityPeriod(activityPeriod)
                    .reportingDefinition(reportingDefinition)
                    .explicitStatus(explicitStatus)
                    .customParameter(customParameter);
    }

    public List<ExperimentVariant> prepareExperimentVariants() {
        return isFullOn()
                ? prepareFullOnVariants()
                : concat(prepareInternalVariants().stream(), prepareNormalVariants().stream())
                        .collect(toList());
    }

    private List<ExperimentVariant> prepareFullOnVariants() {
        return allVariantNames()
                .stream()
                .map(this::convertToFullOnVariant)
                .collect(toList());
    }

    private ExperimentVariant convertToFullOnVariant(String variantName) {
        List<Predicate> predicates = new ArrayList<>();
        if (variantName.equals(fullOnVariantName)) {
            predicates.add(new FullOnPredicate());
            if (deviceClass != DeviceClass.all) {
                predicates.add(new DeviceClassPredicate(deviceClass.toJsonString()));
            }
        }
        return new ExperimentVariant(variantName, predicates);
    }

    private List<ExperimentVariant> prepareInternalVariants() {
        return internalVariantName != null
                ? List.of(new ExperimentVariant(internalVariantName, ImmutableList.of(new InternalPredicate())))
                : List.of();
    }

    private List<ExperimentVariant> prepareNormalVariants() {
        if (percentage != null && !variantNames.isEmpty()) {
            int maxPercentageVariant = 100 / variantNames.size();
            if (percentage > maxPercentageVariant) {
                throw new ExperimentDefinitionException(
                        String.format("Percentage exceeds maximum value (%s > %s)",
                        percentage, maxPercentageVariant));
            }
            return IntStream.range(0, variantNames.size())
                    .mapToObj(i -> convertVariantByIndex(i, maxPercentageVariant))
                    .collect(toList());
        }  else {
            return List.of();
        }
    }

    public ZonedDateTime getLastStatusChange() {
        if (isActive()) {
            return getActivityPeriod().getActiveFrom();
        }

        if (lastExplicitStatusChange != null) {
            return getLastExplicitStatusChange();
        }

        if (isEnded()) {
            return getActivityPeriod().getActiveTo();
        }

        return null;
    }

    private static String emptyToNull(String s) {
        if (s != null && s.isEmpty()) {
            return null;
        }
        return s;
    }
}
