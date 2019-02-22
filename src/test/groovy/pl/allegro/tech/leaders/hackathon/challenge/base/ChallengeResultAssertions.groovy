package pl.allegro.tech.leaders.hackathon.challenge.base

import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult

import static pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult

class ChallengeResultAssertions {
    static ChallengeResultAssertions expectChallengeResult(List<TaskResult> results, String teamId, String challengeId) {
        List<TaskResult> filtered = results.findAll {
            it.teamId == teamId && it.challengeId == challengeId
        }
        assert filtered.size() > 0
        return new ChallengeResultAssertions(filtered)
    }

    static ChallengeResultAssertions expectChallengeResult(List<TaskResult> results) {
        return new ChallengeResultAssertions(results)
    }

    private final List<TaskResult> results

    private ChallengeResultAssertions(List<TaskResult> results) {
        this.results = results
    }

    ChallengeResultAssertions hasSize(int count) {
        assert results.size() == count
        return this
    }

    ChallengeResultAssertions hasMaxScores(ChallengeDefinition challenge) {
        challenge.tasks.each {
            assert findTaskResult(it.name).score == it.maxPoints
        }
        return this
    }

    ChallengeResultAssertions hasMaxScoreForTask(TaskWithFixedResult taskDefinition) {
        TaskResult taskResult = findTaskResult(taskDefinition.name)
        assert taskResult != null
        assert taskResult.score == taskDefinition.getMaxPoints()
        return this
    }

    ChallengeResultAssertions hasZeroScores(ChallengeDefinition challenge) {
        challenge.tasks.each {
            assert findTaskResult(it.name).score == 0
        }
        return this
    }

    ChallengeResultAssertions hasZeroScoreForTask(TaskWithFixedResult task) {
        TaskResult taskResult = findTaskResult(task.name)
        assert taskResult != null
        assert taskResult.score == 0
        return this
    }

    ChallengeResultAssertions hasNonZeroScoreForTask(TaskDefinition task) {
        TaskResult taskResult = findTaskResult(task.name)
        assert taskResult != null
        assert taskResult.score > 0
        return this
    }

    private TaskResult findTaskResult(String taskName) {
        TaskResult taskResult = results.find { it.taskName == taskName }
        assert taskResult != null
        return taskResult
    }
}
