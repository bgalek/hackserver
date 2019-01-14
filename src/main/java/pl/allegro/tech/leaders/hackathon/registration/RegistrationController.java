package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;

import javax.servlet.ServletRequest;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/registration")
class RegistrationController {

    private static final String REGISTRATION_TOPIC = "/topic/registration";
    private static final String CLIENT_CONNECTED = "/hello";

    private final RegistrationService registrationService;

    RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    @MessageMapping(CLIENT_CONNECTED)
    @SendTo(REGISTRATION_TOPIC)
    Set<RegisteredTeamResponse> getRegisteredTeams() {
        return registrationService.getAll()
                .stream()
                .map(registeredTeam -> new RegisteredTeamResponse(registeredTeam.getName()))
                .collect(Collectors.toSet());
    }

    @PostMapping
    ResponseEntity addTeam(@RequestBody RegisterTeamRequest registerTeamRequest, ServletRequest servletRequest) {
        String remoteAddr = servletRequest.getRemoteAddr();
        TeamToRegister teamToRegister = new TeamToRegister(registerTeamRequest.getName(), remoteAddr);
        RegisteredTeam registeredTeam = registrationService.register(teamToRegister);
        return ResponseEntity.created(URI.create(String.format("/teams/%s", registeredTeam.getId()))).build();
    }
}
