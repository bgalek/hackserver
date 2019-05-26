package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import pl.allegro.tech.leaders.hackathon.registration.api.HealthCheckClient;

@Configuration
class RegistrationConfiguration {

    @Bean
    TeamRepository teamRepository(ApplicationEventPublisher applicationEventPublisher,
                                  PersistenceTeamRepository persistenceTeamRepository) {
        return new TeamRepository(persistenceTeamRepository, applicationEventPublisher);
    }

    @Bean
    RegistrationFacade registrationFacade(TeamRepository teamRepository, HealthCheckMonitor healthCheckMonitor) {
        return new RegistrationFacade(teamRepository, healthCheckMonitor);
    }

    @Bean
    HealthCheckMonitor healthCheckMonitor(HealthCheckClient healthCheckClient, ApplicationEventPublisher applicationEventPublisher) {
        return new HealthCheckMonitor(healthCheckClient, applicationEventPublisher);
    }

    @Bean
    HealthCheckSchedule healthCheckSchedule(TaskScheduler taskScheduler,
                                            TeamRepository teamRepository,
                                            HealthCheckMonitor healthCheckMonitor,
                                            HealthCheckConfigurationProperties healthCheckConfigurationProperties) {
        return new HealthCheckSchedule(taskScheduler, teamRepository, healthCheckMonitor, healthCheckConfigurationProperties);
    }
}
