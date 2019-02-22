package pl.allegro.tech.leaders.hackathon.challenge;

import java.util.Map;

public interface TaskDefinition {
    int getMaxPoints();
    String getName();
    Map<String, String> getParams();
    int scoreSolution(Object solution);

    static TaskWithFixedResult withFixedResult(String name, Map<String, String> params, Object expectedResult, int maxPoints) {
        return new TaskWithFixedResult(name, params, expectedResult, maxPoints);
    }

    class TaskWithFixedResult implements TaskDefinition {
        private final String name;
        private final Map<String, String> params;
        private final int maxPoints;
        private final Object expectedSolution;

        TaskWithFixedResult(String name, Map<String, String> params, Object expectedSolution, int maxPoints) {
            this.name = name;
            this.params = params;
            this.maxPoints = maxPoints;
            this.expectedSolution = expectedSolution;
        }

        public Object getExpectedSolution() {
            return expectedSolution;
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
        public int scoreSolution(Object solution) {
            if (solution == null) {
                return 0;
            }
            return expectedSolution.equals(solution)
                    ? maxPoints
                    : 0;
        }
    }
}

