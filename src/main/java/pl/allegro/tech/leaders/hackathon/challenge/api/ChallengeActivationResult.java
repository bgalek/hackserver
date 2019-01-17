package pl.allegro.tech.leaders.hackathon.challenge.api;

public class ChallengeActivationResult {
    public static ChallengeActivationResult active(String challengeId) {
        return new ChallengeActivationResult(challengeId, true);
    }

    private final String challengeId;
    private final boolean activated;

    public ChallengeActivationResult(String challengeId, boolean activated) {
        this.challengeId = challengeId;
        this.activated = activated;
    }

    public String getChallengeId() {
        return challengeId;
    }

    public boolean isActivated() {
        return activated;
    }
}
