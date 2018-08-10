package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.PropertyName;
import org.javers.core.metamodel.annotation.TypeName;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentDefinitionException;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentOrigin;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;

@TypeName("Experiment")
public class ExperimentDefinition {
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
    private final Boolean editable;
    private final ExperimentStatus explicitStatus;
    private final ExperimentStatus status;
    private final ReportingDefinition reportingDefinition;
    private final CustomParameter customParameter;

    private ExperimentDefinition(
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
            Boolean editable,
            ExperimentStatus explicitStatus,
            ReportingDefinition reportingDefinition,
            CustomParameter customParameter) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(variantNames);
        Preconditions.checkNotNull(reportingDefinition);
        Preconditions.checkNotNull(groups);
        Preconditions.checkNotNull(deviceClass);
        Preconditions.checkArgument(internalVariantName == null || !internalVariantName.isEmpty());
        Preconditions.checkArgument(fullOnVariantName == null || !fullOnVariantName.isEmpty());
        this.id = id;
        this.variantNames = ImmutableList.copyOf(variantNames);
        this.internalVariantName = internalVariantName;
        this.fullOnVariantName = fullOnVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass;
        this.description = emptyToNull(description);
        this.documentLink = emptyToNull(documentLink);
        this.author = author;
        this.groups = ImmutableList.copyOf(groups);
        this.activityPeriod = activityPeriod;
        this.editable = editable;
        this.explicitStatus = explicitStatus;
        this.status = ExperimentStatus.of(explicitStatus, activityPeriod);
        this.reportingDefinition = reportingDefinition;
        this.customParameter = customParameter;
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

    public Boolean getEditable() {
        return editable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof ExperimentDefinition)) return false;

        final var that = (ExperimentDefinition) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
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

    public boolean isAssignable() {
        return !isEnded() && !isPaused();
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
        final ActivityPeriod activityPeriod = this.activityPeriod.endNow();
        return mutate()
                .activityPeriod(activityPeriod)
                .explicitStatus(null)
                .build();
    }

    public ExperimentDefinition pause() {
        return mutate()
                .explicitStatus(ExperimentStatus.PAUSED)
                .build();
    }

    public ExperimentDefinition resume() {
        return mutate()
                .explicitStatus(null)
                .build();
    }

    public ExperimentDefinition makeFullOn(String fullOnVariantName) {
        Preconditions.checkNotNull(fullOnVariantName);
        Preconditions.checkNotNull(this.activityPeriod);
        final ActivityPeriod activityPeriod = this.activityPeriod.endNow();
        return mutate()
                .activityPeriod(activityPeriod)
                .explicitStatus(ExperimentStatus.FULL_ON)
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

    public ExperimentDefinition withEditableFlag(boolean editable) {
        return mutate()
                .editable(editable)
                .build();
    }

    public ExperimentDefinition withOrigin(String origin) {
        Preconditions.checkNotNull(origin);
        return mutate()
                .origin(origin)
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
        if (customParameter != null) {
            predicates.add(new CustomParameterPredicate(customParameter.getName(), customParameter.getValue()));
        }
        return new ExperimentVariant(variantName, predicates);
    }

    public Builder mutate() {
        return Builder.from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    private List<ExperimentVariant> prepareExperimentVariants() {
        return isFullOn()
                ? prepareFullOnVariants()
                : concat(prepareInternalVariants().stream(), prepareNormalVariants().stream())
                        .collect(toList());
    }

    private List<ExperimentVariant> prepareFullOnVariants() {
        return getVariantNamesWithInternal()
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

    private List<String> getVariantNamesWithInternal() {
        return internalVariantName != null && !variantNames.contains(internalVariantName)
                ? concat(variantNames.stream(), Stream.of(internalVariantName)).collect(toList())
                : variantNames;
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

    @Deprecated
    public Experiment toExperiment() {
        try {
            return Experiment.builder()
                    .id(id)
                    .variants(prepareExperimentVariants())
                    .description(description)
                    .documentLink(documentLink)
                    .author(author)
                    .groups(groups)
                    .activityPeriod(activityPeriod)
                    .explicitStatus(explicitStatus)
                    .origin(ExperimentOrigin.MONGO.toString())
                    .definition(this)
                    .build();

        } catch (Exception e) {
            throw new ExperimentDefinitionException("Cannot create experiment from definition, id=" + this.id, e);
        }
    }

    public static class Builder {

        static Builder from(ExperimentDefinition other) {
            return new Builder()
                    .id(other.id)
                    .variantNames(other.variantNames)
                    .internalVariantName(other.internalVariantName)
                    .fullOnVariantName(other.fullOnVariantName)
                    .deviceClass(other.deviceClass)
                    .percentage(other.percentage)
                    .description(other.description)
                    .documentLink(other.documentLink)
                    .author(other.author)
                    .groups(other.groups)
                    .activityPeriod(other.activityPeriod)
                    .editable(other.editable)
                    .reportingDefinition(other.reportingDefinition)
                    .explicitStatus(other.explicitStatus)
                    .customParameter(other.customParameter);
        }

        private String id;
        private List<String> variantNames;
        private String internalVariantName;
        private String fullOnVariantName;
        private Integer percentage;
        private DeviceClass deviceClass = DeviceClass.all;
        private String description;
        private String documentLink;
        private String author;
        private List<String> groups = Collections.emptyList();
        private ActivityPeriod activityPeriod;
        private Boolean editable;
        private String origin;
        private ExperimentStatus explicitStatus;
        private ReportingDefinition reportingDefinition = ReportingDefinition.createDefault();
        private CustomParameter customParameter;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder variantNames(List<String> variantNames) {
            this.variantNames = variantNames;
            return this;
        }

        public Builder reportingDefinition(ReportingDefinition reportingDefinition) {
            this.reportingDefinition = reportingDefinition;
            return this;
        }

        public Builder internalVariantName(String internalVariantName) {
            this.internalVariantName = internalVariantName;
            return this;
        }

        public Builder fullOnVariantName(String fullOnVariantName) {
            this.fullOnVariantName = fullOnVariantName;
            return this;
        }

        public Builder deviceClass(String deviceClass) {
            this.deviceClass = DeviceClass.fromString(deviceClass);
            return this;
        }

        public Builder deviceClass(DeviceClass deviceClass) {
            this.deviceClass = deviceClass;
            return this;
        }

        public Builder percentage(Integer percentage) {
            this.percentage = percentage;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder documentLink(String documentLink) {
            this.documentLink = documentLink;
            return this;
        }

        public Builder author(String author) {
            this.author = author;
            return this;
        }

        public Builder groups(List<String> groups) {
            this.groups = groups;
            return this;
        }

        public Builder activityPeriod(ActivityPeriod activityPeriod) {
            this.activityPeriod = activityPeriod;
            return this;
        }

        public Builder editable(Boolean editable) {
            this.editable = editable;
            return this;
        }

        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder explicitStatus(ExperimentStatus explicitStatus) {
            this.explicitStatus = explicitStatus;
            return this;
        }

        public Builder customParameter(CustomParameter customParameter) {
            this.customParameter = customParameter;
            return this;
        }

        public ExperimentDefinition build() {
            return new ExperimentDefinition(
                    id,
                    variantNames,
                    internalVariantName,
                    fullOnVariantName,
                    percentage,
                    deviceClass,
                    description,
                    documentLink,
                    author,
                    groups,
                    activityPeriod,
                    editable,
                    explicitStatus,
                    reportingDefinition,
                    customParameter);
        }
    }

    static String emptyToNull(String s) {
        if (s != null && s.isEmpty()) {
            return null;
        }
        return s;
    }
}
