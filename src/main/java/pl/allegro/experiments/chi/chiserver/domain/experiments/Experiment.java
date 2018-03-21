package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import joptsimple.internal.Strings;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@TypeName("Experiment")
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
            ExperimentStatus explicitStatus) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(id));
        Preconditions.checkNotNull(variants);
        Preconditions.checkArgument(!variants.isEmpty());
        Preconditions.checkNotNull(groups);
        this.id = id;
        this.variants = ImmutableList.copyOf(variants);
        this.description = description;
        this.documentLink = documentLink;
        this.author = author;
        this.groups = ImmutableList.copyOf(groups);
        this.reportingEnabled = reportingEnabled;
        this.activityPeriod = activityPeriod;
        this.measurements = measurements;
        this.editable = editable;
        this.origin = origin;
        this.explicitStatus = explicitStatus;
        this.status = ExperimentStatus.of(explicitStatus, activityPeriod);
    }

    @Id
    @DiffInclude
    public String getId() {
        ImmutableMap.of("k","v");
        return id;

    }

    public List<ExperimentVariant> getVariants() {
        return variants;
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

    public ExperimentMeasurements getMeasurements() {
        return measurements;
    }

    public Boolean getEditable() {
        return editable;
    }

    public String getOrigin() {
        return origin;
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

    public Experiment start(long experimentDurationDays) {
        final ZonedDateTime from = ZonedDateTime.now();
        final ZonedDateTime to = from.plusDays(experimentDurationDays);
        final ActivityPeriod newActivityPeriod = new ActivityPeriod(from, to);
        return mutate()
                .activityPeriod(newActivityPeriod)
                .build();
    }

    public Experiment stop() {
        final ActivityPeriod activityPeriod = this.activityPeriod.endNow();
        return mutate()
                .activityPeriod(activityPeriod)
                .build();
    }

    public Experiment pause() {
        return mutate()
                .explicitStatus(ExperimentStatus.PAUSED)
                .build();
    }

    public Experiment resume() {
        return mutate()
                .explicitStatus(null)
                .build();
    }

    public Experiment prolong(long experimentAdditionalDays) {
        final ZonedDateTime from = this.activityPeriod.getActiveFrom();
        final ZonedDateTime to = this.activityPeriod.getActiveTo().plusDays(experimentAdditionalDays);
        final ActivityPeriod newActivityPeriod = new ActivityPeriod(from, to);
        return mutate()
                .activityPeriod(newActivityPeriod)
                .build();
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
                    .explicitStatus(other.explicitStatus);
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
                    explicitStatus);
        }
    }
}
