package pl.allegro.tech.leaders.hackathon.challenge.infrastucture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.challenge.api.ChallengeDefinition;

import java.util.List;

@Component
public class InitialChallengeDefinitionRegistrar {
    private static final Logger logger = LoggerFactory.getLogger(InitialChallengeDefinitionRegistrar.class);

    @Autowired
    private ChallengeFacade challengeFacade;

    @Autowired
    private List<ChallengeDefinition> challengeDefinitions;

    @EventListener(ContextRefreshedEvent.class)
    public void registerChallengeDefinitions() {
        logger.info("Registering challenges");
        challengeFacade
                .registerChallengeDefinitions(challengeDefinitions)
                .doOnNext(challenge -> logger.info("Registered challenge: {}, {}, {}", challenge.getId(), challenge.getName(), challenge.getDescription()))
                .blockLast();
    }
}
