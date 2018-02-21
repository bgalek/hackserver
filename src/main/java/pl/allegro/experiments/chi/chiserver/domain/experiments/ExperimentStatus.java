package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.time.ZonedDateTime;

public enum ExperimentStatus {
    DRAFT, PLANNED, ACTIVE, ENDED;

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
}