package pl.allegro.tech.leaders.hackathon.registration;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class RegistrationConfig {

    @Bean
    TeamRepository teamRepository(SpringDataTeamRepository springDataTeamRepository) {
        return new MongoTeamRepository(springDataTeamRepository);
    }

    @Bean
    RegistrationService registrationService(TeamRepository teamRepository, MeterRegistry meterRegistry) {
        return new RegistrationService(teamRepository, meterRegistry);
    }

    @Bean
    RegistrationSchedule registrationSchedule(RegistrationService registrationService) {
        return new RegistrationSchedule(registrationService);
    }
}
