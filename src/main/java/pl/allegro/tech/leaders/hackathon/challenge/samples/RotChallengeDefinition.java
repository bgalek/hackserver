package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
class RotChallengeDefinition implements ChallengeDefinition {

    private static final List<TaskDefinition> TASKS = List.of(
            TaskDefinition.withFixedResult("Should decode mnqnavr cvrejfmr mebovbar cbcenjavr",
                    new LinkedMultiValueMap<>(Map.of("string", List.of("mnqnavr cvrejfmr mebovbar cbcenjavr"))),
                    "zadanie pierwsze zrobione poprawnie",
                    new TaskScoring(20, 1000)),
            TaskDefinition.withFixedResult("Should decode rfiuws norobws nozwqncbs",
                    new LinkedMultiValueMap<>(Map.of("string", List.of("rfiuws norobws nozwqncbs"))),
                    "drugie zadanie zaliczone",
                    new TaskScoring(20, 1000)),
            TaskDefinition.withFixedResult("Should decode eifydhy tuxuhcy tlivcihy jijluqhcy",
                    new LinkedMultiValueMap<>(Map.of("string", List.of("eifydhy tuxuhcy tlivcihy jijluqhcy"))),
                    "kolejne zadanie zrobione poprawnie",
                    new TaskScoring(20, 1000)),
            TaskDefinition.withDynamicResult("Should decode a dynamic rotation", new LinkedMultiValueMap<>(
                            Map.of("string", List.of(() -> rot("a teraz zadanie z losowa podstawa", new Random().nextInt(14) + 1)))),
                    () -> "a teraz zadanie z losowa podstawa",
                    new TaskScoring(30, 1000)
            )
    );

    @Override
    public String getName() {
        return "Rotation Cipher";
    }

    @Override
    public String getDescription() {
        return "Your task is to decrypt text encoded with rotation cipher";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/decrypt";
    }

    @Override
    public Class<String> solutionType() {
        return String.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("string", "message to decrypt")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("Should decode mnqnavr cvrejfmr mebovbar cbcenjavr", new LinkedMultiValueMap<>(Map.of("string", List.of("mnqnavr cvrejfmr mebovbar cbcenjavr"))), "zadanie pierwsze zrobione poprawnie", new TaskScoring(4, 200));
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }

    static String rot(String input, int rotation) {
        StringBuilder encrypted = new StringBuilder(input);
        String alphabetU = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String alphabetL = alphabetU.toLowerCase();
        String shiftedAlphabetU = alphabetU.substring(rotation) + alphabetU.substring(0, rotation);
        String shiftedAlphabetL = alphabetL.substring(rotation) + alphabetL.substring(0, rotation);
        for (int i = 0; i < encrypted.length(); i++) {
            char currChar = encrypted.charAt(i);
            if (alphabetU.indexOf(currChar) != -1) {
                int idx = alphabetU.indexOf(currChar);
                char newChar = shiftedAlphabetU.charAt(idx);
                encrypted.setCharAt(i, newChar);
            }
            if (alphabetL.indexOf(currChar) != -1) {
                int idx = alphabetL.indexOf(currChar);
                char newChar = shiftedAlphabetL.charAt(idx);
                encrypted.setCharAt(i, newChar);
            }
        }
        return encrypted.toString();
    }
}
