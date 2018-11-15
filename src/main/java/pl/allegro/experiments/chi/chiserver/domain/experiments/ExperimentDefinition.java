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
    private final ExperimentGoal goal;
    private final List<ExperimentTag> tags;

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
            ZonedDateTime lastExplicitStatusChange,
            ExperimentGoal goal,
            List<ExperimentTag> tags) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(variantNames);
        Preconditions.checkNotNull(groups);
        Preconditions.checkNotNull(tags);
        Preconditions.checkArgument(internalVariantName == null || !internalVariantName.isEmpty());
        Preconditions.checkArgument(fullOnVariantName == null || !fullOnVariantName.isEmpty());
        this.id = id;
        this.variantNames = ImmutableList.copyOf(variantNames);
        this.internalVariantName = internalVariantName;
        this.fullOnVariantName = fullOnVariantName;
        this.percentage = percentage;
        this.deviceClass = DeviceClass.all == deviceClass ? null : deviceClass;
        this.description = emptyToNull(description);
        this.documentLink = emptyToNull(documentLink);
        this.author = author;
        this.groups = ImmutableList.copyOf(groups);
        this.activityPeriod = activityPeriod;
        this.explicitStatus = explicitStatus;
        this.status = ExperimentStatus.of(explicitStatus, activityPeriod);
        this.reportingDefinition = reportingDefinition == null ? ReportingDefinition.DEFAULT : reportingDefinition;
        this.customParameter = customParameter;
        this.lastExplicitStatusChange = lastExplicitStatusChange;
        this.goal = goal;
        this.tags = tags;
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
    public List<ExperimentTag> getTags() {
        return tags;
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

    public Optional<DeviceClass> getDeviceClass() { return Optional.ofNullable(deviceClass); }

    @DiffInclude
    @PropertyName("deviceClass")
    Optional<String> getDeviceClassLegacyFormat() {
        return getDeviceClass().map(it -> it.toJsonString());
    }

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
    public Optional<CustomParameter> getCustomParameter() {
        return Optional.ofNullable(customParameter);
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

    @DiffInclude
    public Optional<ExperimentGoal> getGoal() {
        return Optional.ofNullable(goal);
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

    public int getNumberOfRegularVariants() {
        return variantNames.size();
    }

    public int getMaxPossibleScaleUp() {
        if (getNumberOfRegularVariants() == 0) {
            return 100;
        }
        return 100/getNumberOfRegularVariants();
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
        return !isEnded() && !isPaused() && !variantNames.isEmpty();
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
                    .customParameter(customParameter)
                    .goal(goal)
                    .tags(tags);
    }

    public List<VariantPercentageAllocation> renderRegularVariantsSolo() {
        if (!hasAnyPercentagePredicate()) {
            return Collections.emptyList();
        }

        final int maxPercentageVariant = 100 / variantNames.size();
        if (percentage > maxPercentageVariant) {
            throw new ExperimentDefinitionException(String.format("Percentage exceeds maximum value (%s > %s)", percentage, maxPercentageVariant));
        }

        return IntStream.range(0, variantNames.size())
                .mapToObj(i -> convertToPercentageAllocationByIndex(i, maxPercentageVariant, percentage))
                .collect(toList());
    }

    public boolean hasAnyPercentagePredicate() {
        return  percentage != null && variantNames.size() > 0 && percentage > 0;
    }

    private VariantPercentageAllocation convertToPercentageAllocationByIndex(int i, int maxPercentageVariant, int percentage) {
        return new VariantPercentageAllocation(variantNames.get(i), new PercentageRange(i * maxPercentageVariant, i * maxPercentageVariant + percentage));
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
