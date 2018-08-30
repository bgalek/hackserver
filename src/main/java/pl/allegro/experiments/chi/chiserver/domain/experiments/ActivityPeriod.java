package pl.allegro.experiments.chi.chiserver.domain.experiments;

import com.google.common.base.Preconditions;

import java.time.ZonedDateTime;
import java.util.Objects;

public class ActivityPeriod {
    private final ZonedDateTime activeFrom;
    private final ZonedDateTime activeTo;

    public ActivityPeriod(ZonedDateTime activeFrom, ZonedDateTime activeTo) {
        Preconditions.checkNotNull(activeFrom);
        Preconditions.checkNotNull(activeTo);
        Preconditions.checkArgument(activeTo.isAfter(activeFrom) || activeTo.equals(activeFrom));
        this.activeFrom = activeFrom.withNano(0).withFixedOffsetZone();
        this.activeTo = activeTo.withNano(0).withFixedOffsetZone();
    }

    public ZonedDateTime getActiveFrom() {
        return activeFrom;
    }

    public ZonedDateTime getActiveTo() {
        return activeTo;
    }

    public ActivityPeriod endNow() {
        final ZonedDateTime newActiveTo = ZonedDateTime.now();
        //minus 1 sec is to force ENDED status after calling this method
        if (activeTo.isAfter(activeFrom)) {
            newActiveTo.minusSeconds(1);
        }
        return new ActivityPeriod(activeFrom, newActiveTo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActivityPeriod that = (ActivityPeriod) o;
        return Objects.equals(activeFrom, that.activeFrom) &&
               Objects.equals(activeTo, that.activeTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activeFrom, activeTo);
    }
}
