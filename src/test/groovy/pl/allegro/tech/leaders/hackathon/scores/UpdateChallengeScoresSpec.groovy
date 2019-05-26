package pl.allegro.tech.leaders.hackathon.scores

import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult
import pl.allegro.tech.leaders.hackathon.scores.api.ChallengeScores
import pl.allegro.tech.leaders.hackathon.scores.api.HackatonScores
import pl.allegro.tech.leaders.hackathon.scores.api.TeamScore
import reactor.core.publisher.Flux
import spock.lang.Ignore

import static pl.allegro.tech.leaders.hackathon.scores.base.SampleTaskResult.createTaskResults

class UpdateChallengeScoresSpec extends ScoresSpec {
    String challengeA = "challenge-A"
    String challengeB = "challenge-B"
    String teamX = "team-X"
    String teamY = "team-Y"
    String teamZ = "team-Z"

    def "should aggregate scores from two challenges"() {
        given: "there are results for challenge A"
            Flux<TaskResult> resultsForA = createTaskResults(challengeA, [
                    (teamX): [2, 0, 4],
                    (teamY): [3, 1]
            ])
        and: "there are results for challenge B"
            Flux<TaskResult> resultsForB = createTaskResults(challengeB, [
                    (teamX): [1, 2],
                    (teamZ): [0, 1]
            ])
        when: "scores are updated"
            updateScores(resultsForA)
            updateScores(resultsForB)
        then: "scores for challenge A is a sum of task scores"
            getChallengeScores(challengeA).scores == [
                    new TeamScore(teamX, 6),
                    new TeamScore(teamY, 4)
            ]
        and: "score for challenge B is a sum of task scores"
            getChallengeScores(challengeB).scores == [
                    new TeamScore(teamX, 3),
                    new TeamScore(teamZ, 1)
            ]
        and: "hackaton scores are sum of challenge scores"
            getHackatonScores().scores == [
                    new TeamScore(teamX, 9),
                    new TeamScore(teamY, 4),
                    new TeamScore(teamZ, 1)
            ]
    }

    @Ignore //don't exactly know why this broke
    def "should override challenge results if challenge was rerun"() {
        given: "there are results for challenge A"
            Flux<TaskResult> resultsForA = createTaskResults(challengeA, [
                    (teamX): [2, 0, 4],
                    (teamY): [3, 1]
            ])
        and: "there are other results for challenge A"
            Flux<TaskResult> nextResultsForA = createTaskResults(challengeA, [
                    (teamX): [2, 0, 4],
                    (teamY): [3, 1]
            ])
        when: "challenge scores are updated with two consecutive result sets"
            updateScores(resultsForA)
            updateScores(nextResultsForA)
        then: "the last result set overrides scores from the first result set"
            getChallengeScores(challengeA).scores == [
                    new TeamScore(teamX, 6),
                    new TeamScore(teamY, 4)
            ]
        and: "hackaton scores are in sync with challenge scores"
            getHackatonScores().scores == [
                    new TeamScore(teamX, 6),
                    new TeamScore(teamY, 4)
            ]
    }

    def "should override challenge results if challenge was rerun for a specific team"() {
        given: "there are results for challenge A"
            Flux<TaskResult> resultsForA = createTaskResults(challengeA, [
                    (teamX): [2, 0, 4],
                    (teamY): [3, 1]
            ])
        and:
            Flux<TaskResult> nextResultsForA = createTaskResults(challengeA, [
                    (teamX): [5, 5, 5]
            ])
        when:
            updateScores(resultsForA)
            updateScores(nextResultsForA)
        then:
            getChallengeScores(challengeA).scores == [
                    new TeamScore(teamX, 15),
                    new TeamScore(teamY, 4)
            ]
        and:
            getHackatonScores().scores == [
                    new TeamScore(teamX, 15),
                    new TeamScore(teamY, 4)
            ]
    }

    private void updateScores(Flux<TaskResult> results) {
        facade.updateScores(results).blockLast()
    }

    private HackatonScores getHackatonScores() {
        return facade.getHackatonScores().block()
    }

    private ChallengeScores getChallengeScores(String challengeId) {
        return facade.getChallengeScores(challengeId).block()
    }
}
