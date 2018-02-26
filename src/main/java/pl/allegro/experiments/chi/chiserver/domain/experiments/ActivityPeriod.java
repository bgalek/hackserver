package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;

public class ActivityPeriod {
    private final ZonedDateTime activeFrom;
    private final ZonedDateTime activeTo;

    public ActivityPeriod(ZonedDateTime activeFrom, ZonedDateTime activeTo) {
        Preconditions.checkNotNull(activeFrom);
        Preconditions.checkNotNull(activeTo);
        Preconditions.checkArgument(activeTo.isAfter(activeFrom) || activeTo.equals(activeFrom));
        this.activeFrom = activeFrom;
        this.activeTo = activeTo;
    }

    public ZonedDateTime getActiveFrom() {
        return activeFrom;
    }

    public ZonedDateTime getActiveTo() {
        return activeTo;
    }

    public ActivityPeriod endNow() {
        return new ActivityPeriod(activeFrom, ZonedDateTime.now());
    }

}
