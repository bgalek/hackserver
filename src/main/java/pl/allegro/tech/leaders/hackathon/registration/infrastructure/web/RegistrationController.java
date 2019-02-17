package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;
import java.net.URI;

@RestController
@RequestMapping("/registration")
class RegistrationController {

    private final RegistrationFacade registrationFacade;

    RegistrationController(RegistrationFacade registrationFacade) {
        this.registrationFacade = registrationFacade;
    }

    @GetMapping
    Publisher<RegisteredTeamResponse> getRegisteredTeams() {
        return registrationFacade.getAll()
                .map(registeredTeam -> new RegisteredTeamResponse(registeredTeam.getName()));
    }

    @PostMapping
    Publisher<ResponseEntity<RegisteredTeamResponse>> registerTeam(HttpServletRequest request, @RequestBody RegisterTeamRequest registerTeamRequest) {
        String name = registerTeamRequest.getName();
        InetSocketAddress remoteAddress = new InetSocketAddress(request.getRemoteAddr(), request.getRemotePort());
        TeamRegistration teamRegistration = new TeamRegistration(name, remoteAddress);
        return registrationFacade.register(teamRegistration)
                .map(registeredTeam -> ResponseEntity.created(URI.create(String.format("/teams/%s", registeredTeam.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build());
    }

}
