package pl.allegro.tech.leaders.hackathon.challenge.base

import groovy.transform.CompileStatic

import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

@CompileStatic
class UpdatableFixedClock extends Clock {
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of('Europe/Warsaw')
    public static final Instant DEFAULT_FIXED_TIME = Instant.parse("2017-11-20T00:00:00.000Z")

    static final UpdatableFixedClock defaultClock(String now = DEFAULT_FIXED_TIME) {
        return new UpdatableFixedClock(Instant.parse(now), DEFAULT_ZONE_ID)
    }

    private final ZoneId zoneId
    private Instant fixedTime

    private UpdatableFixedClock(Instant fixedTime, ZoneId zoneId) {
        this.fixedTime = Objects.requireNonNull(fixedTime)
        this.zoneId = Objects.requireNonNull(zoneId)
    }

    @Override
    ZoneId getZone() {
        return zoneId
    }

    @Override
    UpdatableFixedClock withZone(ZoneId zone) {
        return new UpdatableFixedClock(this.fixedTime, zone)
    }

    @Override
    Instant instant() {
        return fixedTime
    }

    Instant futureInstant(Duration duration = Duration.ofDays(1)) {
        return this.fixedTime + duration
    }

    Instant pastInstant(Duration duration = Duration.ofDays(1)) {
        return this.fixedTime - duration
    }

    void reset() {
        this.fixedTime = DEFAULT_FIXED_TIME
    }

    void tick(Duration duration = Duration.ofMillis(1)) {
        this.fixedTime = this.fixedTime + duration
    }

    void changeFixedTime(String currentTime) {
        this.fixedTime = Instant.parse(currentTime)
    }

    void changeFixedTime(Instant currentTime) {
        this.fixedTime = currentTime
    }
}
