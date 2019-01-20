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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Random;

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
    Publisher<ResponseEntity<RegisteredTeamResponse>> registerTeam(@RequestBody RegisterTeamRequest registerTeamRequest) {
        String name = registerTeamRequest.getName();
        // when wiremock is on classpath it causes spring to start using jetty instead of netty
        // in netty we need to use serverHttpRequest (no servlet) in jetty we use servletHttpRequest (servlet)
        // random just for now...
        InetSocketAddress remoteAddress = new InetSocketAddress(new Random().nextInt((9999 - 9000) + 1) + 9000);
        TeamRegistration teamRegistration = new TeamRegistration(name, remoteAddress);
        return registrationFacade.register(teamRegistration)
                .map(registeredTeam -> ResponseEntity.created(URI.create(String.format("/teams/%s", registeredTeam.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .build());
    }

}
