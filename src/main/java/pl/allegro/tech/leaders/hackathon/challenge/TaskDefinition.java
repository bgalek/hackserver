package pl.allegro.tech.leaders.hackathon.challenge;

import java.util.Map;

public interface TaskDefinition {

    String getName();

    Map<String, String> getParameters();

    TaskScoring getTaskScoring();

    int scoreSolution(Object solution);

    static TaskWithFixedResult withFixedResult(String name, Map<String, String> params, Object expectedResult, TaskScoring taskScoring) {
        return new TaskWithFixedResult(name, params, expectedResult, taskScoring);
    }

    class TaskWithFixedResult implements TaskDefinition {
        private final String name;
        private final Map<String, String> parameters;
        private final Object expectedSolution;
        private final TaskScoring taskScoring;

        TaskWithFixedResult(String name, Map<String, String> parameters, Object expectedSolution, TaskScoring taskScoring) {
            this.name = name;
            this.parameters = parameters;
            this.expectedSolution = expectedSolution;
            this.taskScoring = taskScoring;
        }

        public Object getExpectedSolution() {
            return expectedSolution;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Map<String, String> getParameters() {
            return parameters;
        }

        @Override
        public TaskScoring getTaskScoring() {
            return taskScoring;
        }

        @Override
        public int scoreSolution(Object solution) {
            if (solution == null) {
                return 0;
            }
            return expectedSolution.equals(solution)
                    ? taskScoring.getMaxPoints()
                    : 0;
        }
    }
}

