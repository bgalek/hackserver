package pl.allegro.tech.leaders.hackathon.registration;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
class RegistrationConfig {

    @Bean
    TeamRepository teamRepository(SpringDataTeamRepository springDataTeamRepository) {
        return new MongoTeamRepository(springDataTeamRepository);
    }

    @Bean
    RegistrationEvents registrationEvents(SimpMessagingTemplate simpMessagingTemplate) {
        return new RegistrationEvents(simpMessagingTemplate);
    }

    @Bean
    RegistrationService registrationService(TeamRepository teamRepository, MeterRegistry meterRegistry, RegistrationEvents registrationEvents) {
        return new RegistrationService(teamRepository, meterRegistry, registrationEvents);
    }

    @Bean
    RegistrationSchedule registrationSchedule(RegistrationService registrationService) {
        return new RegistrationSchedule(registrationService);
    }
}
