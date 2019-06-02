package pl.allegro.tech.leaders.hackathon.challenge.samples

import org.springframework.expression.Expression
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import spock.lang.Specification

class CalculatorChallengeDefinitionSpec extends Specification {
    def 'should be able to solve all calculator challenge tasks'() {
        given:
            CalculatorChallengeDefinition challenge = new CalculatorChallengeDefinition()
        when:
            List results = challenge.getTasks().collect {
                it.scoreSolution(solve(it.getParameters().get("equation")[0]))
            }
        then:
            results.every { 1 }
    }

    private static String solve(String question) {
        ExpressionParser expressionParser = new SpelExpressionParser()
        Expression expression = expressionParser.parseExpression(question)
        return expression.getValue() as String
    }
}
