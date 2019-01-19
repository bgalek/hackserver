package pl.allegro.tech.leaders.hackathon.registration;

import org.springframework.context.ApplicationEventPublisher;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;
import pl.allegro.tech.leaders.hackathon.registration.events.TeamRegisteredEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RegistrationService {

    private final ApplicationEventPublisher publisher;
    private final TeamRepository teamRepository;

    RegistrationService(ApplicationEventPublisher publisher, TeamRepository teamRepository) {
        this.publisher = publisher;
        this.teamRepository = teamRepository;
    }

    public Mono<RegisteredTeam> register(TeamRegistration teamRegistration) {
        Team team = new Team(teamRegistration.getName(), teamRegistration.getRegistrationIp());
        return teamRepository.save(team)
                .map(this::toRegisteredTeam)
                .doOnSuccess(registeredTeam -> publisher.publishEvent(new TeamRegisteredEvent(registeredTeam)));
    }

    public Flux<RegisteredTeam> getAll() {
        return teamRepository.findAll()
                .map(this::toRegisteredTeam);
    }

    public Mono<RegisteredTeam> getTeamByName(String name) {
        return teamRepository.findByName(name)
                .map(this::toRegisteredTeam);
    }

    private RegisteredTeam toRegisteredTeam(Team team) {
        return new RegisteredTeam(team.getName());
    }
}
