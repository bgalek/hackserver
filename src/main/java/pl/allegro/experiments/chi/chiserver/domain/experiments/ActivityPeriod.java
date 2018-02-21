package pl.allegro.experiments.chi.chiserver.domain.experiments;

import java.time.ZonedDateTime;

public class ActivityPeriod {
    private final ZonedDateTime activeFrom;
    private final ZonedDateTime activeTo;

    public ActivityPeriod(ZonedDateTime activeFrom, ZonedDateTime activeTo) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActivityPeriod that = (ActivityPeriod) o;

        if (!activeFrom.equals(that.activeFrom)) return false;
        return activeTo.equals(that.activeTo);
    }

    @Override
    public int hashCode() {
        int result = activeFrom.hashCode();
        result = 31 * result + activeTo.hashCode();
        return result;
    }
}
