package pl.allegro.tech.leaders.hackathon.registration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class RegistrationService {

    private final TeamRepository teamRepository;
    private final Counter registrationCounter;

    RegistrationService(TeamRepository teamRepository, MeterRegistry registry) {
        this.teamRepository = teamRepository;
        this.registrationCounter = registry.counter("registration.created");
    }

    RegisteredTeam register(TeamToRegister teamToRegister) {
        registrationCounter.increment();
        Team team = new Team(teamToRegister.getName(), teamToRegister.getRegistrationIp());
        teamRepository.save(team);
        return new RegisteredTeam(teamToRegister.getName());
    }

    List<RegisteredTeam> getAll() {
        return teamRepository.findAll()
                .stream()
                .map(it -> new RegisteredTeam(it.getName()))
                .collect(toList());
    }
}
