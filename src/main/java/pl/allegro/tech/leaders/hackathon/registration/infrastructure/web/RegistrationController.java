package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamSecret;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamUpdate;
import pl.allegro.tech.leaders.hackathon.utils.InetUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;

import static reactor.core.publisher.Mono.just;

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
                .map(registeredTeam -> new RegisteredTeamResponse(
                        registeredTeam.getName(),
                        registeredTeam.getRemoteAddress().getAddress().getCanonicalHostName(),
                        registeredTeam.getRemoteAddress().getPort()
                ));
    }

    @PostMapping
    Publisher<ResponseEntity<String>> registerTeam(HttpServletRequest request, @RequestBody RegisterTeamRequest registerTeamRequest) {
        String name = registerTeamRequest.getName();
        int port = registerTeamRequest.getPort();
        InetAddress remoteAddress = InetUtils.fromString(request.getRemoteAddr());
        TeamRegistration teamRegistration = new TeamRegistration(name, new InetSocketAddress(remoteAddress, port));
        return registrationFacade.register(teamRegistration)
                .map(registeredTeam -> ResponseEntity.created(URI.create(String.format("/teams/%s", registeredTeam.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(registeredTeam.getSecret()));
    }

    @PatchMapping("/{teamName}")
    Publisher<ResponseEntity> updateTeam(HttpServletRequest request,
                                         @PathVariable String teamName,
                                         @RequestBody UpdateTeamRequest updateTeamRequest,
                                         @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        TeamSecret secret = TeamSecret.fromAuthorizationHeader(authorization);
        if (!secret.isValid()) return just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        int port = updateTeamRequest.getPort();
        InetAddress remoteAddress = InetUtils.fromString(request.getRemoteAddr());
        TeamUpdate teamUpdate = new TeamUpdate(teamName, new InetSocketAddress(remoteAddress, port), secret);
        return registrationFacade.update(teamUpdate).then(just(ResponseEntity.accepted().build()));
    }

}
