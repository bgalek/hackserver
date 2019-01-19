package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationService;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;

import java.net.URI;

@RestController
@RequestMapping("/registration")
class RegistrationController {

    private final RegistrationService registrationService;

    RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    Publisher<RegisteredTeamResponse> getRegisteredTeams() {
        return registrationService.getAll()
                .map(registeredTeam -> new RegisteredTeamResponse(registeredTeam.getName()));
    }

    @PostMapping
    Publisher<ResponseEntity<RegisteredTeamResponse>> registerTeam(@RequestBody RegisterTeamRequest registerTeamRequest) {
        TeamRegistration teamRegistration = new TeamRegistration(registerTeamRequest.getName(), "localhost:8080");
        return registrationService.register(teamRegistration)
                .map(registeredTeam -> ResponseEntity.created(URI.create(String.format("/teams/%s", registeredTeam.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build());
    }

}
