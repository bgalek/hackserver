package pl.allegro.tech.leaders.hackathon.challenge;

import java.util.List;

class ChallengeDetailsResponse {

    private final String name;
    private final String description;
    private final List<String> examples;

    ChallengeDetailsResponse(Challenge challenge) {
        this.name = challenge.getName();
        this.description = challenge.getDescription();
        this.examples = challenge.getExamples();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getExamples() {
        return examples;
    }
}
