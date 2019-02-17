package pl.allegro.tech.leaders.hackathon.challenge;

import java.util.Map;

/**
 * @param <T> solution type
 */
public interface ChallengeTaskDefinition<T> {
    int getMaxPoints();
    String getName();
    Map<String, String> getParams();
    int scoreSolution(T solution);

    static ChallengeTaskDefinition<String> withFixedResult(String name, Map<String, String> params, String expectedResult, int maxPoints) {
        return new ChallengeTaskWithFixedResult(name, params, expectedResult, maxPoints);
    }
}

class ChallengeTaskWithFixedResult implements ChallengeTaskDefinition<String> {
    private final String name;
    private final Map<String, String> params;
    private final int maxPoints;
    private final String expectedSolution;

    public ChallengeTaskWithFixedResult(String name, Map<String, String> params, String expectedSolution, int maxPoints) {
        this.name = name;
        this.params = params;
        this.maxPoints = maxPoints;
        this.expectedSolution = expectedSolution;
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
    public int scoreSolution(String solution) {
        if (solution == null) {
            return 0;
        }
        return expectedSolution.trim().equalsIgnoreCase(solution)
                ? maxPoints
                : 0;
    }
}

