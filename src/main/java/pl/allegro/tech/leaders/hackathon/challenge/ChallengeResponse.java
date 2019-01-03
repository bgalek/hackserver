package pl.allegro.tech.leaders.hackathon.challenge;

class ChallengeResponse {
    private final String id;
    private final String name;
    private final String description;

    ChallengeResponse(Challenge challenge) {
        this.id = challenge.getId();
        this.name = challenge.getName();
        this.description = challenge.getDescription();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
