package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RegistrationConfiguration {
    @Bean
    RegistrationFacade registrationService(PersistenceTeamRepository persistenceTeamRepository) {
        TeamRepository teamRepository = new TeamRepository(persistenceTeamRepository);
        return new RegistrationFacade(teamRepository);
    }
}
