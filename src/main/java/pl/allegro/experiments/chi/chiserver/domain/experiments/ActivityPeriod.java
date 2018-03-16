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
        this.activeFrom = activeFrom.withNano(0);
        this.activeTo = activeTo.withNano(0);
    }

    public ZonedDateTime getActiveFrom() {
        return activeFrom;
    }

    public ZonedDateTime getActiveTo() {
        return activeTo;
    }

    public ActivityPeriod endNow() {
        //minus 1 sec is to force ENDED status after calling this method
        return new ActivityPeriod(activeFrom, ZonedDateTime.now().minusSeconds(1));
    }

}
