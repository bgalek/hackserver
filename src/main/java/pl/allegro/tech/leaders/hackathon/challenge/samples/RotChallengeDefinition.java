package pl.allegro.tech.leaders.hackathon.challenge.samples;

import com.grayen.encryption.caesar.algorithm.Caesar;
import com.grayen.encryption.caesar.algorithm.implementation.CaesarFabric;
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

    private static final Caesar CAESAR = CaesarFabric.getEncryptionSystem();
    private static final Random RANDOM = new Random();

    private static final List<TaskDefinition> TASKS = List.of(
            TaskDefinition.withFixedResult("Should decode mnqnavr cvrejfmr mebovbar cbcenjavr", new LinkedMultiValueMap<>(Map.of("string", List.of("mnqnavr cvrejfmr mebovbar cbcenjavr"))), "zadanie pierwsze zrobione poprawnie", new TaskScoring(5, 100)),
            TaskDefinition.withFixedResult("Should decode rfiuws norobws nozwqncbs", new LinkedMultiValueMap<>(Map.of("string", List.of("rfiuws norobws nozwqncbs"))), "drugie zadanie zaliczone", new TaskScoring(5, 100)),
            TaskDefinition.withFixedResult("Should decode eifydhy tuxuhcy tlivcihy jijluqhcy", new LinkedMultiValueMap<>(Map.of("string", List.of("eifydhy tuxuhcy tlivcihy jijluqhcy"))), "kolejne zadanie zrobione poprawnie", new TaskScoring(5, 100)),
            TaskDefinition.withDynamicResult("Should decode a dynamic rotation", new LinkedMultiValueMap<>(
                            Map.of("string", List.of(() -> CAESAR.encrypt("a teraz zadanie z losową podstawą", RANDOM.nextInt(14))))),
                    () -> "a teraz zadanie z losową podstawą",
                    new TaskScoring(50, 100)
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
}
