package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface TaskDefinition {

    String getName();

    MultiValueMap<String, String> getParameters();

    Object getExpectedSolution();

    boolean isParametersEncoded();

    TaskScoring getTaskScoring();

    int scoreSolution(Object solution);

    static TaskWithFixedResult withFixedResult(String name, MultiValueMap<String, String> params, Object expectedResult, TaskScoring taskScoring) {
        return new TaskWithFixedResult(name, params, expectedResult, taskScoring, false);
    }

    static TaskWithFixedResult withFixedResult(String name, MultiValueMap<String, String> params, Object expectedResult, TaskScoring taskScoring, boolean encoded) {
        return new TaskWithFixedResult(name, params, expectedResult, taskScoring, encoded);
    }

    static TaskWithDynamicResult withDynamicResult(String name, MultiValueMap<String, Supplier<String>> params, Supplier<Object> expectedResult, TaskScoring taskScoring) {
        return new TaskWithDynamicResult(name, params, expectedResult, taskScoring, false);
    }

    static TaskWithDynamicResult withDynamicResult(String name, MultiValueMap<String, Supplier<String>> params, Supplier<Object> expectedResult, TaskScoring taskScoring, boolean encoded) {
        return new TaskWithDynamicResult(name, params, expectedResult, taskScoring, encoded);
    }

    static <T> TaskWithValidableResult<T> withValidateResult(String name, T testcase, BiFunction<T, String, Boolean> verification, boolean expected, TaskScoring taskScoring) {
        return new TaskWithValidableResult<T>(name, testcase, verification, expected, taskScoring);
    }

    class TaskWithValidableResult<T> implements TaskDefinition {

        private final String name;
        private final T testcase;
        private final BiFunction<T, String, Boolean> verification;
        private final boolean expected;
        private final TaskScoring taskScoring;

        public TaskWithValidableResult(String name, T testcase, BiFunction<T, String, Boolean> verification, boolean expected, TaskScoring taskScoring) {
            this.name = name;
            this.testcase = testcase;
            this.verification = verification;
            this.expected = expected;
            this.taskScoring = taskScoring;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MultiValueMap<String, String> getParameters() {
            return new LinkedMultiValueMap<>(Map.of());
        }

        @Override
        public Boolean getExpectedSolution() {
            return expected;
        }

        @Override
        public boolean isParametersEncoded() {
            return false;
        }

        @Override
        public TaskScoring getTaskScoring() {
            return taskScoring;
        }

        @Override
        public int scoreSolution(Object solution) {
            return verification.apply(testcase, String.valueOf(solution)) == expected ? taskScoring.getMaxPoints() : 0;
        }
    }

    class TaskWithFixedResult implements TaskDefinition {

        private final String name;
        private final MultiValueMap<String, String> parameters;
        private final Object expectedSolution;
        private final TaskScoring taskScoring;
        private final boolean encoded;

        TaskWithFixedResult(String name, MultiValueMap<String, String> parameters, Object expectedSolution, TaskScoring taskScoring, boolean encoded) {
            this.name = name;
            this.parameters = parameters;
            this.expectedSolution = expectedSolution;
            this.taskScoring = taskScoring;
            this.encoded = encoded;
        }

        @Override
        public Object getExpectedSolution() {
            return expectedSolution;
        }

        @Override
        public boolean isParametersEncoded() {
            return encoded;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MultiValueMap<String, String> getParameters() {
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

    class TaskWithDynamicResult implements TaskDefinition {

        private final String name;
        private final MultiValueMap<String, Supplier<String>> parameters;
        private final Object expectedSolution;
        private final TaskScoring taskScoring;
        private final boolean encoded;

        TaskWithDynamicResult(String name, MultiValueMap<String, Supplier<String>> parameters, Supplier<Object> expectedSolution, TaskScoring taskScoring, boolean encoded) {
            this.name = name;
            this.parameters = parameters;
            this.expectedSolution = expectedSolution.get();
            this.taskScoring = taskScoring;
            this.encoded = encoded;
        }

        @Override
        public Object getExpectedSolution() {
            return expectedSolution;
        }

        @Override
        public boolean isParametersEncoded() {
            return encoded;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MultiValueMap<String, String> getParameters() {
            LinkedMultiValueMap<String, String> result = new LinkedMultiValueMap<>();
            parameters.forEach((key, value) -> result.add(key, value.stream().map(Supplier::get).collect(Collectors.joining())));
            return result;
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

