package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class WordBlenderChallengeDefinition implements ChallengeDefinition {
    private static final List<TaskWithFixedResult> TASKS = List.of(
            TaskDefinition.withFixedResult(
                    "Should be able to create few whole words",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            "developer",
                            "reedlvoep",
                            "vrpoleeed",
                            "derelveop",
                            "developer"
                    ))),
                    5,
                    new TaskScoring(10, 1000)
            ),
            TaskDefinition.withFixedResult(
                    "Should be able to handle more words",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            "restoration",
                            "slave",
                            "colorful",
                            "fatality",
                            "knot",
                            "staircase",
                            "catalogue",
                            "announcement",
                            "quarrel",
                            "first",
                            "criticism",
                            "reservoir",
                            "incident",
                            "switch",
                            "frog",
                            "link",
                            "cassette",
                            "vision",
                            "holiday",
                            "hard",
                            "hyqnothize",
                            "evaluate",
                            "royalty",
                            "deviation",
                            "available"
                    ))),
                    0,
                    new TaskScoring(10, 1000)
            ),
            TaskDefinition.withFixedResult(
                    "Should be able to create word with null byte",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            "develope\u0000",
                            "r"
                    ))),
                    1,
                    new TaskScoring(15, 1000)
            ),

            TaskDefinition.withFixedResult(
                    "Should be able to distinguish foreign alphabets",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            URLEncoder.encode("ⅾeveⅼореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("deveⅼореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾevelореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼpреr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼоpеr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼорer", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼореr", StandardCharsets.UTF_8),
                            URLEncoder.encode("ⅾeveⅼoреr", StandardCharsets.UTF_8),
                            URLEncoder.encode("😀💻🔥", StandardCharsets.UTF_8))
                    )),
                    1,
                    new TaskScoring(25, 1000),
                    true
            ),
            TaskDefinition.withFixedResult(
                    "Should be able to work with emoji typed",
                    new LinkedMultiValueMap<>(Map.of("word", List.of(
                            URLEncoder.encode("🎫📷💼🔤🌝 🏉📥👧📇🔌 👇👂🎨👫",StandardCharsets.UTF_8),
                            URLEncoder.encode("🕥🍞🏤 👫🐪🔡🐠💊💪.",StandardCharsets.UTF_8),
                            URLEncoder.encode("😀💻🔥🌓 🍆🌜🔣👦 👀🐌",StandardCharsets.UTF_8),
                            URLEncoder.encode("📭🐭🏆 📝🕂🔝🏇💄 🍁🎆🍵🐉👖🐲🕠 🕧🌌💥💷📕 📞🌊🐪📎",StandardCharsets.UTF_8),
                            URLEncoder.encode("🐧🌰 👦🍻💡📵🕂🎓 🍝👽🕞🌴🔻 👾🎁🎰👙🍴 🐣👽🏯🌱👑 🌹📪👕",StandardCharsets.UTF_8),
                            URLEncoder.encode("📖💅 🌗🐅🎓🐢🌑🌚🍈 📊📃💵🍻🌒 📆🕁🐴🔓🔛📺 📬",StandardCharsets.UTF_8),
                            URLEncoder.encode("💓🔒📐🍤🔈🍹. 🔷🔐🍝🍍👬 🌻🐘🎆🔒🌴 💬🎭🎃📼",StandardCharsets.UTF_8),
                            URLEncoder.encode("🎫📷💼🔤🌝 🏉📥👧📇🔌 👇👂🎨👫",StandardCharsets.UTF_8),
                            URLEncoder.encode("🕥🍞🏤 👫🐪🔡🐠💊💪.",StandardCharsets.UTF_8),
                            URLEncoder.encode("🕥🍞🏤 👫🐪🔡🐠💊💪.",StandardCharsets.UTF_8),
                            URLEncoder.encode("😀💻🔥🌓 🍆🌜🔣👦 👀🐌",StandardCharsets.UTF_8),
                            URLEncoder.encode("📭🐭🏆 📝🕂🔝🏇💄 🍁🎆🍵🐉👖🐲🕠 🕧🌌💥💷📕 📞🌊🐪📎",StandardCharsets.UTF_8),
                            URLEncoder.encode("🐧🌰 👦🍻💡📵🕂🎓 🍝👽🕞🌴🔻 👾🎁🎰👙🍴 🐣👽🏯🌱👑 🌹📪👕",StandardCharsets.UTF_8),
                            URLEncoder.encode("📖💅 🌗🐅🎓🐢🌑🌚🍈 📊📃💵🍻🌒 📆🕁🐴🔓🔛📺 📬",StandardCharsets.UTF_8),
                            URLEncoder.encode("💓🔒📐🍤🔈🍹. 🔷🔐🍝🍍👬 🌻🐘🎆🔒🌴 💬🎭🎃📼",StandardCharsets.UTF_8)
                    ))),
                    0,
                    new TaskScoring(20, 1000),
                    true
            )
    );

    @Override
    public String getName() {
        return "Word Blender";
    }

    @Override
    public String getDescription() {
        return "You will receive a set of words, break them into separate letters in a blender. " +
                "Your task is to count how many times word \"developer\" can be created simply by " +
                "taking the letters form the blender one by one.";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/blender";
    }

    @Override
    public Class<Integer> solutionType() {
        return Integer.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("word", "word to blend")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult(
                "8 words",
                new LinkedMultiValueMap<>(Map.of("word", List.of(
                        "devday",
                        "code",
                        "hacker",
                        "java",
                        "danger",
                        "allegro",
                        "paperclip",
                        "lullaby"
                ))),
                2,
                new TaskScoring(5, 200)
        );
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
