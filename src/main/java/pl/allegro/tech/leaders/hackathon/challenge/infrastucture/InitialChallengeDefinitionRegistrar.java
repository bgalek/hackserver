package pl.allegro.tech.leaders.hackathon.challenge.infrastucture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;

import java.util.List;

@Component
public class InitialChallengeDefinitionRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(InitialChallengeDefinitionRegistrar.class);

    private final ChallengeFacade challengeFacade;
    private final List<ChallengeDefinition> challengeDefinitions;

    InitialChallengeDefinitionRegistrar(ChallengeFacade challengeFacade, List<ChallengeDefinition> challengeDefinitions) {
        this.challengeFacade = challengeFacade;
        this.challengeDefinitions = challengeDefinitions;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void registerChallengeDefinitions() {
        logger.info("Registering challenges");
        challengeFacade
                .registerChallengeDefinitions(challengeDefinitions)
                .doOnNext(challenge -> logger.info("Registered challenge: {}, {}, {}", challenge.getId(), challenge.getName(), challenge.getDescription()))
                .blockLast();
    }
}
