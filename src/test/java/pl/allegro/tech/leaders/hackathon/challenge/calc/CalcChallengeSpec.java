package pl.allegro.tech.leaders.hackathon.challenge.calc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
class CalcChallengeSpec {

    @Test
    @DisplayName("should be able to solve all calculator challenge tasks")
    void solveChallenge() {
        // given
        CalcChallenge challenge = new CalcChallenge(new ObjectMapper());

        // expect
        challenge.getTasks().forEach((task) -> assertTrue(task.checkAnswer(solve(task.getQuestion()))));
    }

    private String solve(String question) {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        try {
            return engine.eval(question).toString();
        } catch (ScriptException e) {
            return null;
        }
    }
}