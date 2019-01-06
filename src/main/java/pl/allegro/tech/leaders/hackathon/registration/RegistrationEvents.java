package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.messaging.simp.SimpMessagingTemplate;

class RegistrationEvents {

    private SimpMessagingTemplate simpMessagingTemplate;

    RegistrationEvents(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    void newTeam(RegisteredTeam registeredTeam) {
        RegisteredTeamResponse payload = new RegisteredTeamResponse(registeredTeam.getName());
        simpMessagingTemplate.convertAndSend("/topic/registration/new", payload);
    }
}
