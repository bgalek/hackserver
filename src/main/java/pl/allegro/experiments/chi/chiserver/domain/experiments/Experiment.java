package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import joptsimple.internal.Strings;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Is it a ClientExperiment? Or maybe StashExperimentDefinition?
 */
@Deprecated
public class Experiment {
    private final String id;
    private final List<ExperimentVariant> variants;
    private final String description;
    private final String documentLink;
    private final String author;
    private final List<String> groups;
    private final ActivityPeriod activityPeriod;
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
            ActivityPeriod activityPeriod,
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
        this.activityPeriod = activityPeriod;
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

    public ReportingDefinition getReportingDefinition() {
        // TODO refactor
        return getDefinition()
                .map(ExperimentDefinition::getReportingDefinition)
                .orElse(ReportingDefinition.createDefault()); // stash experiment or mongo experiment without reporting type
    }

    public boolean shouldSaveInteractions() {
        return !isFullOn();
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

    public ExperimentStatus getExplicitStatus() {
        return this.explicitStatus;
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

    public boolean isFullOn() {
        return getStatus() == ExperimentStatus.FULL_ON;
    }

    public boolean isAssignable() {
        return !isEnded() && !isPaused();
    }

    public boolean isEffectivelyEnded() {
        return isEnded() || isFullOn();
    }

    public boolean isEndable() {
        return isActive() || isFullOn();
    }

    public Builder mutate() {
        return Builder.from(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean hasCustomParam() {
        return getDefinition().map(ExperimentDefinition::getCustomParameter).isPresent();
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
                    .activityPeriod(other.activityPeriod)
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
        private ActivityPeriod activityPeriod;
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
                    activityPeriod,
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
