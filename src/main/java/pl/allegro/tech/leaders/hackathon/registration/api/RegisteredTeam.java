package pl.allegro.tech.leaders.hackathon.registration.api;

import java.net.URI;

public class RegisteredTeam {
    // TODO: setup team uri
    private final URI uri = URI.create("http://localhost:8080");
    private final String name;

    public RegisteredTeam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return name;
    }

    public URI getUri() {
        return uri;
    }
}
