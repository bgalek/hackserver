package pl.allegro.tech.leaders.hackathon.challenge.samples

import spock.lang.Specification

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

class CalcChallengeDefinitionSpec extends Specification {
    def 'should be able to solve all calculator challenge tasks'() {
        given:
            CalcChallengeDefinition challenge = new CalcChallengeDefinition()
        when:
            List results = challenge.getTasks().collect {
                it.scoreSolution(solve(it.getParams().get("equation")))
            }
        then:
            results.every { 1 }
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
