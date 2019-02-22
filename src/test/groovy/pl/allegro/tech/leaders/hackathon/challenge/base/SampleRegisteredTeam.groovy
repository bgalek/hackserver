package pl.allegro.tech.leaders.hackathon.challenge.base

import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam
import pl.allegro.tech.leaders.hackathon.utils.InetUtils

import java.util.concurrent.atomic.AtomicInteger

class SampleRegisteredTeam {
    static AtomicInteger counter = new AtomicInteger(0)
    static RegisteredTeam TEAM_A = sampleRegisteredTeam("team-A")
    static RegisteredTeam TEAM_B = sampleRegisteredTeam("team-B")
    static RegisteredTeam TEAM_C = sampleRegisteredTeam("team-C")

    static RegisteredTeam sampleRegisteredTeam(String name) {
        int uniqueNumber = counter.incrementAndGet()
        String secret = "secret-${name}"
        InetAddress address = InetUtils.fromString("127.0.1." + uniqueNumber)
        return new RegisteredTeam(name, address, secret)
    }
}
