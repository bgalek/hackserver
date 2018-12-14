package pl.allegro.tech.leaders.hackathon.registration;

class TeamToRegister {
    private final String name;
    private final String registrationIp;

    TeamToRegister(String name, String registrationIp) {

        this.name = name;
        this.registrationIp = registrationIp;
    }

    String getName() {
        return name;
    }

    String getRegistrationIp() {
        return registrationIp;
    }
}
