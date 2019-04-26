package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@ConfigurationProperties(prefix = "healthcheck")
class HealthCheckConfigurationProperties {

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration rate = Duration.ofSeconds(1);

    @DurationUnit(ChronoUnit.MILLIS)
    private Duration delay = Duration.ofSeconds(1);

    public Duration getRate() {
        return rate;
    }

    public void setRate(Duration rate) {
        this.rate = rate;
    }

    public Duration getDelay() {
        return delay;
    }

    public void setDelay(Duration delay) {
        this.delay = delay;
    }
}
