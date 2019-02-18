package pl.allegro.tech.leaders.hackathon.registration.infrastructure.web;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;
import pl.allegro.tech.leaders.hackathon.utils.InetUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Objects;

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
                        registeredTeam.getRemoteAddress().getCanonicalHostName()
                ));
    }

    @PostMapping
    Publisher<ResponseEntity<String>> registerTeam(HttpServletRequest request, @RequestBody RegisterTeamRequest registerTeamRequest) {
        String name = registerTeamRequest.getName();
        TeamRegistration teamRegistration = new TeamRegistration(name, InetUtils.fromString(request.getRemoteAddr()));
        return registrationFacade.register(teamRegistration)
                .map(registeredTeam -> ResponseEntity.created(URI.create(String.format("/teams/%s", registeredTeam.getId())))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .body(registeredTeam.getSecret()));
    }

    @DeleteMapping("/{teamName}")
    Publisher<ResponseEntity> unregisterTeam(@PathVariable String teamName,
                                             @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (Objects.isNull(authorization) || authorization.isEmpty() || authorization.split(" ")[1].isEmpty())
            return just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
        String secret = authorization.split(" ")[1];
        return registrationFacade.unregister(teamName, secret).then(just(ResponseEntity.accepted().build()));
    }

}
