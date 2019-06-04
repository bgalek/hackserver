package pl.allegro.tech.leaders.hackathon.challenge.infrastucture.jmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.challenge.api.TaskResult;
import reactor.core.publisher.Mono;

/*
 * JMX operations must return serializable result for JConsole
 */
@Service
@ManagedResource
public class ChallengeJmxResource {
    private final ChallengeFacade challengeFacade;
    private final ObjectMapper objectMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ChallengeJmxResource(ChallengeFacade challengeFacade, ObjectMapper objectMapper) {
        this.challengeFacade = challengeFacade;
        this.objectMapper = objectMapper;
    }

    @ManagedOperation
    public String activateChallenge(String challengeId) {
        return toJson(challengeFacade.activateChallenge(challengeId));
    }

    @ManagedOperation
    public String deactivateChallenge(String challengeId) {
        return toJson(challengeFacade.deactivateChallenge(challengeId));
    }

    @ManagedOperation
    public String runChallenge(String challengeId) {
        challengeFacade.runChallenge(challengeId)
                .doOnNext(this::logResult)
                .blockLast();
        return "Challenge executed: " + challengeId;
    }

    @ManagedOperation
    public String runChallenge(String challengeId, String teamId) {
        challengeFacade.runChallenge(challengeId, teamId)
                .doOnNext(this::logResult)
                .blockLast();
        return "Challenge executed: " + challengeId + " for team: " + teamId;
    }

    private void logResult(TaskResult result) {
        logger.info("Got task result: {}", result);
    }

    private String toJson(Mono<?> mono) {
        return toJson(mono.block());
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
        } catch (Exception e) {
            throw new IllegalArgumentException("Could not serialize object to JSON", e);
        }
    }
}
