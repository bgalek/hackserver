package pl.allegro.tech.leaders.hackathon.challenge.api;

import java.util.Map;

public interface ChallengeTask {
    int getMaxPoints();
    String getName();
    Map<String, String> getParams();
    ChallengeTaskResult scoreSolution(String solution);

    static ChallengeTask withFixedResult(String name, Map<String, String> params, String expectedResult, int maxPoints) {
        return new ChallengeTaskWithFixedResult(name, params, expectedResult, maxPoints);
    }

    static ChallengeTask withFixedResult(String name, Map<String, String> params, String expectedResult) {
        return withFixedResult(name, params, expectedResult, 1);
    }
}

class ChallengeTaskWithFixedResult implements ChallengeTask {
    private final String name;
    private final Map<String, String> params;
    private final int maxPoints;
    private final String expectedResult;

    public ChallengeTaskWithFixedResult(String name, Map<String, String> params, String expectedResult, int maxPoints) {
        this.name = name;
        this.params = params;
        this.maxPoints = maxPoints;
        this.expectedResult = expectedResult;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public int getMaxPoints() {
        return maxPoints;
    }

    @Override
    public ChallengeTaskResult scoreSolution(String solution) {
        int points = expectedResult.equals(solution)
                ? maxPoints
                : 0;
        return new ChallengeTaskResult(name, params, maxPoints, points);
    }
}

