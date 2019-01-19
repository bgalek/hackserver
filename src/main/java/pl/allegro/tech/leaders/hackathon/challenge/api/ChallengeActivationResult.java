package pl.allegro.tech.leaders.hackathon.challenge.api;

public class ChallengeActivationResult {
    public static ChallengeActivationResult activated(String challengeId) {
        return new ChallengeActivationResult(challengeId, true);
    }

    public static ChallengeActivationResult deactivated(String challengeId) {
        return new ChallengeActivationResult(challengeId, false);
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
