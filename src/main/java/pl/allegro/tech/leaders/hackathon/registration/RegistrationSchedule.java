package pl.allegro.tech.leaders.hackathon.registration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

class RegistrationSchedule {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationSchedule.class);
    private final RegistrationService registrationService;

    RegistrationSchedule(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Scheduled(fixedRate = 3000)
    void removeZombies() {
        registrationService.getAll().forEach(registeredTeam -> {
            logger.info("checked helth of {}", registeredTeam);
        });
    }
}
