package pl.allegro.tech.leaders.hackathon.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationService;
import pl.allegro.tech.leaders.hackathon.registration.api.RegisteredTeam;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/run")
public class TaskRunnerController {
    private final RegistrationService registrationService;
    private final ChallengeFacade challengeFacade;
    private final TaskRunner taskRunner;

    @Autowired
    public TaskRunnerController(RegistrationService registrationService, ChallengeFacade challengeFacade, TaskRunner taskRunner) {
        this.registrationService = registrationService;
        this.challengeFacade = challengeFacade;
        this.taskRunner = taskRunner;
    }

    @GetMapping("/example/{id}")
    Mono<TaskResult> runExampleTask(@PathVariable("id") String challengeId,
                                 @RequestParam("team-id") String teamId) {

        Mono<RegisteredTeam> registeredTeam = registrationService.getTeamByName(teamId);
        Mono<ChallengeDefinition> taskDef = challengeFacade.getActiveChallengeDefinition(challengeId);

        return Mono.zip(taskDef, registeredTeam)
                .flatMap(tuple -> taskRunner.run(tuple.getT1(), tuple.getT1().getExample(), tuple.getT2()));
    }
}
