package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;

public enum ExperimentStatus {
    DRAFT(false), PLANNED(false), ACTIVE(false), ENDED(false), PAUSED(true), FULL_ON(true);

    boolean explicit;

    ExperimentStatus(boolean explicit) {
        this.explicit = explicit;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public ExperimentStatus explicitOrNull() {
        if (isExplicit()) {
            return this;
        }
        return null;
    }

    public static ExperimentStatus of(ActivityPeriod activityPeriod) {
        if (activityPeriod == null) {
            return ExperimentStatus.DRAFT;
        } else if (activityPeriod.getActiveTo().isBefore(ZonedDateTime.now())) {
            return ExperimentStatus.ENDED;
        } else if (activityPeriod.getActiveFrom().isAfter(ZonedDateTime.now())) {
            return ExperimentStatus.PLANNED;
        } else {
            return ExperimentStatus.ACTIVE;
        }
    }

    public static ExperimentStatus of(ExperimentStatus status, ActivityPeriod activityPeriod) {
        Preconditions.checkArgument(status == null || status.isExplicit(), "Explicit experiment explicitStatus can be only PAUSED OR FULL_ON");
        if (status == null) {
            return of(activityPeriod);
        }
        return status;
    }
}