package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.concurrent.TimeUnit;

class HealthCheckSchedule {

    private TeamRepository teamRepository;
    private HealthCheckMonitor healthCheckMonitor;

    HealthCheckSchedule(TaskScheduler taskScheduler,
                        TeamRepository teamRepository,
                        HealthCheckMonitor healthCheckMonitor,
                        HealthCheckConfigurationProperties healthCheckConfigurationProperties) {
        this.teamRepository = teamRepository;
        this.healthCheckMonitor = healthCheckMonitor;
        long rate = healthCheckConfigurationProperties.getRate().toMillis();
        long delay = healthCheckConfigurationProperties.getDelay().toMillis();
        taskScheduler.schedule(this::checkHealth, getTrigger(rate, delay, TimeUnit.MILLISECONDS));
    }

    private PeriodicTrigger getTrigger(long rate, long delay, TimeUnit timeUnit) {
        PeriodicTrigger periodicTrigger = new PeriodicTrigger(rate, timeUnit);
        periodicTrigger.setInitialDelay(delay);
        return periodicTrigger;
    }

    private void checkHealth() {
        teamRepository.findAll().subscribe(team -> healthCheckMonitor.updateHealth(team).block());
    }
}
