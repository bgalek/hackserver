package pl.allegro.tech.leaders.hackathon.registration;

import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RegistrationFacade {

    private final TeamRepository teamRepository;

    RegistrationFacade(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Mono<RegisteredTeam> register(TeamRegistration teamRegistration) {
        Team team = new Team(teamRegistration.getName(), teamRegistration.getRemoteAddress());
        return teamRepository.save(team)
                .map(this::toRegisteredTeam);
    }

    public Flux<RegisteredTeam> getAll() {
        return teamRepository.findAll()
                .map(this::toRegisteredTeam);
    }

    public Mono<RegisteredTeam> getTeamByName(String name) {
        return teamRepository.findByName(name)
                .map(this::toRegisteredTeam)
                .switchIfEmpty(Mono.error(new TeamNotFoundException(name)));
    }

    private RegisteredTeam toRegisteredTeam(Team team) {
        return new RegisteredTeam(team.getName());
    }
}
