package pl.allegro.tech.leaders.hackathon.registration.api;

public class TeamRegistration {
    private final String name;
    private final String registrationIp;

    public TeamRegistration(String name, String registrationIp) {

        this.name = name;
        this.registrationIp = registrationIp;
    }

    public String getName() {
        return name;
    }

    public String getRegistrationIp() {
        return registrationIp;
    }
}
