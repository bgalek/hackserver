package pl.allegro.tech.leaders.hackathon.scores.base

import groovy.transform.CompileStatic
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult
import reactor.core.publisher.Flux

@CompileStatic
class SampleTaskResult {
    private static final Map<String, ?> DEFAULT_PROPERTIES = [
            teamId            : "team-id",
            challengeId       : "challenge-id",
            taskName          : "task-name",
            score             : 1,
            responseBody      : "respnse-body",
            responseHttpStatus: 200,
            latencyMillis     : 25
    ]

    static TaskResult sampleTaskResult(Map<String, ?> customProperties) {
        Map<String, ?> properties = DEFAULT_PROPERTIES + customProperties
        return new TaskResult(
                properties.teamId as String,
                properties.challengeId as String,
                properties.taskName as String,
                properties.responseBody as String,
                properties.responseHttpStatus as Integer,
                properties.score as Integer,
                properties.latencyMillis as Long,
                properties.errorMessage as String
        )
    }

    static Flux<TaskResult> createTaskResults(String challengeId, Map<String, List<Integer>> scoresByTeamId) {
        List<TaskResult> results = scoresByTeamId.entrySet()
                .collectMany { entry ->
            entry.value.collect {
                sampleTaskResult(challengeId: challengeId, teamId: entry.key, score: it)
            }
        }
        return Flux.fromIterable(results)
    }
}
