package pl.allegro.tech.leaders.hackathon.challenge.samples

import javax.script.ScriptEngine
import javax.script.ScriptEngineManager
import javax.script.ScriptException

import static pl.allegro.tech.leaders.hackathon.configuration.ObjectMapperProvider.objectMapper

class CalcChallengeSpec {
    def 'should be able to solve all calculator challenge tasks'() {
        given:
            CalcChallenge challenge = new CalcChallenge(objectMapper())
        when:
            List<Boolean> results = challenge.getTasks().collect {
                it.checkAnswer(solve(it.getQuestion()))
            }
        then:
            results.each { assert it }
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