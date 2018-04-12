package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;
import pl.allegro.experiments.chi.chiserver.domain.experiments.administration.ExperimentDefinitionException;
import pl.allegro.experiments.chi.chiserver.infrastructure.experiments.ExperimentOrigin;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@TypeName("Experiment")
public class ExperimentDefinition {
    private final String id;
    private final List<String> variantNames;
    private final String internalVariantName;
    private final Integer percentage;
    private final String deviceClass;
    private final String description;
    private final String documentLink;
    private final String author;
    private final List<String> groups;
    private final boolean reportingEnabled;
    private final ActivityPeriod activityPeriod;
    private final Boolean editable;
    private final ExperimentStatus explicitStatus;
    private final ExperimentStatus status;

    private final ReportingDefinition reportingDefinition;

    private ExperimentDefinition(
            String id,
            List<String> variantNames,
            String internalVariantName,
            Integer percentage,
            String deviceClass,
            String description,
            String documentLink,
            String author,
            List<String> groups,
            boolean reportingEnabled,
            ActivityPeriod activityPeriod,
            Boolean editable,
            ExperimentStatus explicitStatus,
            ReportingDefinition reportingDefinition) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(variantNames);
        Preconditions.checkNotNull(reportingDefinition);
        Preconditions.checkNotNull(groups);
        Preconditions.checkArgument(internalVariantName == null || !internalVariantName.isEmpty());
        Preconditions.checkArgument(deviceClass == null || !deviceClass.isEmpty());
        this.id = id;
        this.variantNames = ImmutableList.copyOf(variantNames);
        this.internalVariantName = internalVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass;
        this.description = emptyToNull(description);
        this.documentLink = emptyToNull(documentLink);
        this.author = author;
        this.groups = ImmutableList.copyOf(groups);
        this.reportingEnabled = reportingEnabled;
        this.activityPeriod = activityPeriod;
        this.editable = editable;
        this.explicitStatus = explicitStatus;
        this.status = ExperimentStatus.of(explicitStatus, activityPeriod);
        this.reportingDefinition = reportingDefinition;
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
    public Optional<String> getInternalVariantName() { return Optional.ofNullable(internalVariantName); };

    @DiffInclude
    public Optional<Integer> getPercentage() {
        return Optional.ofNullable(percentage);
    }

    @DiffInclude
    public Optional<String> getDeviceClass() { return Optional.ofNullable(deviceClass); }

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

    @DiffInclude
    public boolean getReportingEnabled() {
        return reportingEnabled;
    }

    public ActivityPeriod getActivityPeriod() {
        return activityPeriod;
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

        ExperimentDefinition that = (ExperimentDefinition) o;
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

    public boolean isAssignable() {
        return !isEnded() && !isPaused();
    }

    public ExperimentDefinition start(long experimentDurationDays) {
        final ZonedDateTime from = ZonedDateTime.now();
        final ZonedDateTime to = from.plusDays(experimentDurationDays);
        final ActivityPeriod newActivityPeriod = new ActivityPeriod(from, to);
        return mutate()
                .activityPeriod(newActivityPeriod)
                .build();
    }

    public ExperimentDefinition stop() {
        final ActivityPeriod activityPeriod = this.activityPeriod.endNow();
        return mutate()
                .activityPeriod(activityPeriod)
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

    public ExperimentDefinition prolong(long experimentAdditionalDays) {
        final ZonedDateTime from = this.activityPeriod.getActiveFrom();
        final ZonedDateTime to = this.activityPeriod.getActiveTo().plusDays(experimentAdditionalDays);
        final ActivityPeriod newActivityPeriod = new ActivityPeriod(from, to);
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

    private ExperimentVariant convertVariant(String variantName, int from, int to) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(new HashRangePredicate(new PercentageRange(from, to)));
        if (deviceClass != null) {
            predicates.add(new DeviceClassPredicate(deviceClass));
        }
        return new ExperimentVariant(variantName, predicates);
    }

    public Builder mutate() {
        return Builder.from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Experiment toExperiment() {
        try {
            List<ExperimentVariant> experimentVariants = new ArrayList<>();

            if (internalVariantName != null) {
                experimentVariants.add(new ExperimentVariant(internalVariantName, ImmutableList.of(new InternalPredicate())));
            }

            if (percentage != null) {
                int maxPercentageVariant = 100 / variantNames.size();
                if (percentage > maxPercentageVariant) {
                    throw new ExperimentDefinitionException("Percentage exceeds maximum value ( " + percentage + " > " + maxPercentageVariant + " )");
                }
                for (int i = 0; i < variantNames.size(); i++) {
                    experimentVariants.add(convertVariant(variantNames.get(i), i * maxPercentageVariant, i * maxPercentageVariant + percentage));
                }
            }

            return Experiment.builder()
                    .id(this.id)
                    .variants(experimentVariants)
                    .description(this.description)
                    .documentLink(this.documentLink)
                    .author(this.author)
                    .groups(this.groups)
                    .reportingEnabled(this.reportingEnabled)
                    .activityPeriod(this.activityPeriod)
                    .explicitStatus(this.explicitStatus)
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
                    .deviceClass(other.deviceClass)
                    .percentage(other.percentage)
                    .description(other.description)
                    .documentLink(other.documentLink)
                    .author(other.author)
                    .groups(other.groups)
                    .reportingEnabled(other.reportingEnabled)
                    .activityPeriod(other.activityPeriod)
                    .editable(other.editable)
                    .reportingDefinition(other.reportingDefinition)
                    .explicitStatus(other.explicitStatus);
        }

        private String id;
        private List<String> variantNames;
        private String internalVariantName;
        private Integer percentage;
        private String deviceClass;
        private String description;
        private String documentLink;
        private String author;
        private List<String> groups = Collections.emptyList();
        private boolean reportingEnabled;
        private ActivityPeriod activityPeriod;
        private Boolean editable;
        private String origin;
        private ExperimentStatus explicitStatus;
        private ReportingDefinition reportingDefinition = ReportingDefinition.createDefault();

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

        public Builder deviceClass(String deviceClass) {
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

        public Builder reportingEnabled(boolean reportingEnabled) {
            this.reportingEnabled = reportingEnabled;
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

        public ExperimentDefinition build() {
            return new ExperimentDefinition(
                    id,
                    variantNames,
                    internalVariantName,
                    percentage,
                    deviceClass,
                    description,
                    documentLink,
                    author,
                    groups,
                    reportingEnabled,
                    activityPeriod,
                    editable,
                    explicitStatus,
                    reportingDefinition);
        }
    }

    static String emptyToNull(String s) {
        if (s != null && s.isEmpty()) {
            return null;
        }
        return s;
    }
}
