package pl.allegro.tech.leaders.hackathon.registration;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class RegistrationService {

    private final TeamRepository teamRepository;
    private final Counter registrationCounter;
    private final RegistrationEvents registrationEvents;

    RegistrationService(TeamRepository teamRepository, MeterRegistry registry, RegistrationEvents registrationEvents) {
        this.teamRepository = teamRepository;
        this.registrationCounter = registry.counter("registration.created");
        this.registrationEvents = registrationEvents;
    }

    RegisteredTeam register(TeamToRegister teamToRegister) {
        registrationCounter.increment();
        Team team = new Team(teamToRegister.getName(), teamToRegister.getRegistrationIp());
        teamRepository.save(team);
        RegisteredTeam registeredTeam = new RegisteredTeam(teamToRegister.getName());
        registrationEvents.newTeam(registeredTeam);
        return registeredTeam;
    }

    List<RegisteredTeam> getAll() {
        return teamRepository.findAll()
                .stream()
                .map(it -> new RegisteredTeam(it.getName()))
                .collect(toList());
    }

    Optional<RegisteredTeam> getTeam(String teamId) {
        return teamRepository.findAll()
                .stream()
                .map(it -> new RegisteredTeam(it.getName()))
                .findFirst();
    }
}
