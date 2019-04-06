package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;

@Configuration
class RegistrationConfiguration {

    @Bean
    RegistrationFacade registrationFacade(PersistenceTeamRepository persistenceTeamRepository,
                                          HealthCheckMonitor healthCheckMonitor) {
        return new RegistrationFacade(new TeamRepository(persistenceTeamRepository), healthCheckMonitor);
    }

    @Bean
    HealthCheckMonitor healthCheckMonitor(HealthCheckClient healthCheckClient) {
        return new HealthCheckMonitor(healthCheckClient);
    }

    @Bean
    HealthCheckSchedule healthCheckSchedule(TaskScheduler taskScheduler,
                                            PersistenceTeamRepository persistenceTeamRepository,
                                            HealthCheckMonitor healthCheckMonitor,
                                            HealthCheckConfigurationProperties healthCheckConfigurationProperties) {
        return new HealthCheckSchedule(taskScheduler,
                new TeamRepository(persistenceTeamRepository),
                healthCheckMonitor,
                healthCheckConfigurationProperties
        );
    }
}
