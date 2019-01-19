package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration
class RegistrationConfiguration {

    @Bean
    TeamRepository teamRepository(PersistenceTeamRepository persistenceTeamRepository) {
        return new TeamRepository(persistenceTeamRepository);
    }

    @Bean
    RegistrationService registrationService(TeamRepository teamRepository, ApplicationEventPublisher applicationEventPublisher) {
        return new RegistrationService(applicationEventPublisher, teamRepository);
    }

    @Bean
    RegistrationEventPublisher registrationEventPublisher(Executor executor) {
        return new RegistrationEventPublisher(executor);
    }
}
