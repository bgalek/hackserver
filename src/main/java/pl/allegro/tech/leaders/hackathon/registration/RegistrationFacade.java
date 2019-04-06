package pl.allegro.tech.leaders.hackathon.registration;

import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamNotFoundException;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamRegistration;
import pl.allegro.tech.leaders.hackathon.registration.api.TeamUpdate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class RegistrationFacade {

    private final TeamRepository teamRepository;
    private final HealthCheckMonitor healthCheckMonitor;

    RegistrationFacade(TeamRepository teamRepository, HealthCheckMonitor healthCheckMonitor) {
        this.teamRepository = teamRepository;
        this.healthCheckMonitor = healthCheckMonitor;
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

    public Mono<RegisteredTeam> getTeamById(String id) {
        return teamRepository.findById(id)
                .switchIfEmpty(Mono.error(new TeamNotFoundException(id)))
                .map(this::toRegisteredTeam);
    }

    public Mono<RegisteredTeam> getTeamByName(String name) {
        return teamRepository.find(name)
                .switchIfEmpty(Mono.error(new TeamNotFoundException(name)))
                .map(this::toRegisteredTeam);
    }

    public Mono<Team> update(TeamUpdate teamUpdate) {
        return teamRepository.find(teamUpdate.getName(), teamUpdate.getSecret())
                .switchIfEmpty(Mono.error(new TeamNotFoundException(teamUpdate.getName())))
                .map(team -> new Team(team, teamUpdate.getRemoteAddress()))
                .doOnSuccess(teamRepository::save);
    }

    private RegisteredTeam toRegisteredTeam(Team team) {
        return new RegisteredTeam(
                team.getName(),
                team.getRemoteAddress(),
                team.getSecret(),
                healthCheckMonitor.getTeamStatus(team)
        );
    }
}
