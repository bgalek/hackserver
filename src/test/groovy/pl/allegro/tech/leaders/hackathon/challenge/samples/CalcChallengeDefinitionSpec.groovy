package pl.allegro.tech.leaders.hackathon.challenge.samples

import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeTaskResult
import spock.lang.Specification

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

import static pl.allegro.tech.leaders.hackathon.configuration.ObjectMapperProvider.objectMapper

class CalcChallengeDefinitionSpec extends Specification {
    def 'should be able to solve all calculator challenge tasks'() {
        given:
            CalcChallengeDefinition challenge = new CalcChallengeDefinition(objectMapper())
        when:
            List<ChallengeTaskResult> results = challenge.getTasks().collect {
                it.scoreSolution(solve(it.getParams().get("equation")))
            }
        then:
            results.every { it.solutionValid }
    }

    private String solve(String question) {
        ScriptEngineManager manager = new ScriptEngineManager()
        ScriptEngine engine = manager.getEngineByName('js')
        try {
            return engine.eval(question).toString()
        } catch (ScriptException e) {
            throw new RuntimeException('Could not execute script', e)
        }
    }
}