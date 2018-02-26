package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;

import java.time.ZonedDateTime;
import java.util.List;

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
    private final ExperimentStatus status;

    public Experiment(
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
            String origin) {
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
        this.status = ExperimentStatus.of(activityPeriod);
    }

    public String getId() {
        return id;
    }

    public List<ExperimentVariant> getVariants() {
        return variants;
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

    public ExperimentStatus getStatus() {
        return status;
    }

    public boolean isDraft() {
        return getStatus() == ExperimentStatus.DRAFT;
    }

    public boolean isEnded() {
        return getStatus() == ExperimentStatus.ENDED;
    }

    public boolean isActive() {
        return getStatus() == ExperimentStatus.ACTIVE;
    }

    public boolean isOverridable() {
        return !isEnded();
    }

    public Experiment start(long experimentDurationDays) {
        return new Experiment(
                id,
                variants,
                description,
                documentLink,
                author,
                groups,
                reportingEnabled,
                new ActivityPeriod(ZonedDateTime.now(), ZonedDateTime.now().plusDays(experimentDurationDays)),
                measurements,
                editable,
                origin
        );
    }

    public Experiment stop() {
        return new Experiment(
                id,
                variants,
                description,
                documentLink,
                author,
                groups,
                reportingEnabled,
                activityPeriod.endNow(),
                measurements,
                editable,
                origin
        );
    }

    public Experiment prolong(long experimentAdditionalDays) {
        return new Experiment(
                id,
                variants,
                description,
                documentLink,
                author,
                groups,
                reportingEnabled,
                new ActivityPeriod(activityPeriod.getActiveFrom(), activityPeriod.getActiveTo().plusDays(experimentAdditionalDays)),
                measurements,
                editable,
                origin
        );
    }

    public Experiment withEditableFlag(boolean editable) {
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
                origin
        );
    }

    public Experiment withOrigin(String origin) {
        Preconditions.checkNotNull(origin);
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
                origin
        );
    }
}
