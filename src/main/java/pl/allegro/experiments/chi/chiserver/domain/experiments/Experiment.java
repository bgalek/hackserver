package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Experiment {
    private final String id;
    private final List<ExperimentVariant> variants;
    private final String description;
    private final String documentLink;
    private final String author;
    private final List<String> groups;
    private final boolean reportingEnabled;
    private final ActivityPeriod activityPeriod;
    private final ExperimentMeasurements measurements;
    private final Boolean editable;
    private final String origin;
    private final ExperimentStatus explicitStatus;
    private final ExperimentStatus status;

    private final ExperimentDefinition definition;

    private Experiment(
            String id,
            List<ExperimentVariant> variants,
            String description,
            String documentLink,
            String author,
            List<String> groups,
            boolean reportingEnabled,
            ActivityPeriod activityPeriod,
            ExperimentMeasurements measurements,
            Boolean editable,
            String origin,
            ExperimentStatus explicitStatus,
            ExperimentDefinition definition) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(variants);
        Preconditions.checkArgument(!variants.isEmpty());
        Preconditions.checkNotNull(groups);
        this.id = id;
        this.variants = ImmutableList.copyOf(variants);
        this.description = emptyToNull(description);
        this.documentLink = emptyToNull(documentLink);
        this.author = author;
        this.groups = ImmutableList.copyOf(groups);
        this.reportingEnabled = reportingEnabled;
        this.activityPeriod = activityPeriod;
        this.measurements = measurements;
        this.editable = editable;
        this.origin = origin;
        this.explicitStatus = explicitStatus;
        this.status = ExperimentStatus.of(explicitStatus, activityPeriod);
        this.definition = definition;
    }

    public String getId() {
        return id;
    }

    public List<ExperimentVariant> getVariants() {
        return variants;
    }

    public List<EventDefinition> getEventDefinitions() {
        // TODO refactor
        if (definition != null) {
            return definition.getReportingDefinition().getEventDefinitions();
        } else {
            return ReportingDefinition.createDefault().getEventDefinitions();
        }
    }

    public ReportingType getReportingType() {
        // TODO refactor
        if (definition != null) {
            return definition.getReportingDefinition().getType();
        } else {
            return ReportingDefinition.createDefault().getType();
        }
    }

    public ReportingDefinition getReportingDefinition() {
        // TODO refactor
        if (definition != null) {
            return definition.getReportingDefinition();
        } else {
            return ReportingDefinition.createDefault();
        }
    }

    public String getDescription() {
        return description;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getGroups() {
        return groups;
    }

    public boolean getReportingEnabled() {
        return reportingEnabled;
    }

    public ActivityPeriod getActivityPeriod() {
        return activityPeriod;
    }

    public LocalDateTime getActiveFrom() {
        if (activityPeriod == null) {
            return null;
        }
        return activityPeriod.getActiveFrom().toLocalDateTime();
    }

    public LocalDateTime getActiveTo() {
        if (activityPeriod == null) {
            return null;
        }
        return activityPeriod.getActiveTo().toLocalDateTime();
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public ExperimentMeasurements getMeasurements() {
        return measurements;
    }

    public Boolean getEditable() {
        return editable;
    }

    public String getOrigin() {
        return origin;
    }

    public Optional<ExperimentDefinition> getDefinition() {
        return Optional.ofNullable(definition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Experiment)) return false;

        Experiment that = (Experiment) o;
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

    public Experiment withEditableFlag(boolean editable) {
        return mutate()
                .editable(editable)
                .build();
    }

    public Experiment withOrigin(String origin) {
        Preconditions.checkNotNull(origin);
        return mutate()
                .origin(origin)
                .build();
    }

    public Builder mutate() {

        return Builder.from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        static Builder from(Experiment other) {
            return new Builder()
                    .id(other.id)
                    .variants(other.variants)
                    .description(other.description)
                    .documentLink(other.documentLink)
                    .author(other.author)
                    .groups(other.groups)
                    .reportingEnabled(other.reportingEnabled)
                    .activityPeriod(other.activityPeriod)
                    .measurements(other.measurements)
                    .editable(other.editable)
                    .origin(other.origin)
                    .explicitStatus(other.explicitStatus)
                    .definition(other.definition);
        }

        private String id;
        private List<ExperimentVariant> variants;
        private String description;
        private String documentLink;
        private String author;
        private List<String> groups;
        private boolean reportingEnabled;
        private ActivityPeriod activityPeriod;
        private ExperimentMeasurements measurements;
        private Boolean editable;
        private String origin;
        private ExperimentStatus explicitStatus;
        private ExperimentDefinition definition;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder variants(List<ExperimentVariant> variants) {
            this.variants = variants;
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

        public Builder measurements(ExperimentMeasurements measurements) {
            this.measurements = measurements;
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

        public Builder definition(ExperimentDefinition definition) {
            this.definition = definition;
            return this;
        }

        public Experiment build() {
            return new Experiment(
                    id,
                    variants,
                    description,
                    documentLink,
                    author,
                    groups,
                    reportingEnabled,
                    activityPeriod,
                    measurements,
                    editable,
                    origin,
                    explicitStatus,
                    definition);
        }
    }

    static String emptyToNull(String s) {
        if (s != null && s.isEmpty()) {
            return null;
        }
        return s;
    }
}
