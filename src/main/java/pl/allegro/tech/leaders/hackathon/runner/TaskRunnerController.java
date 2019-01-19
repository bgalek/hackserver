package pl.allegro.tech.leaders.hackathon.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.registration.Team;
import pl.allegro.tech.leaders.hackathon.registration.TeamRepository;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/run")
public class TaskRunnerController {
    private final TeamRepository teamRepository;
    private final ChallengeFacade challengeFacade;
    private final TaskRunner taskRunner;

    @Autowired
    public TaskRunnerController(TeamRepository teamRepository, ChallengeFacade challengeFacade, TaskRunner taskRunner) {
        this.teamRepository = teamRepository;
        this.challengeFacade = challengeFacade;
        this.taskRunner = taskRunner;
    }

    @GetMapping("/example/{id}")
    Mono<Integer> runExampleTask(@PathVariable("id") String challengeId,
                                 @RequestParam("team-id") String teamId) {

        Team team = teamRepository.get(teamId);
        Mono<ChallengeDefinition> taskDef = challengeFacade.getActiveChallengeDefinition(challengeId);

        Mono<TaskResult> result = taskDef.flatMap(t -> taskRunner.run(t, t.getExample(), team));

        return result.map(it -> it.getScore());
    }
}
