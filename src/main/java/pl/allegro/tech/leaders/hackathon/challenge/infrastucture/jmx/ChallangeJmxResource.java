package pl.allegro.tech.leaders.hackathon.challenge.infrastucture.jmx;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeFacade;
import pl.allegro.tech.leaders.hackathon.utils.ReactorLoggingSubscriber;
import reactor.core.publisher.Mono;

/*
 * JMX operations must return serializable result for JConsole
 */
@Service
@ManagedResource
public class ChallangeJmxResource {
    private final ChallengeFacade challengeFacade;
    private final ObjectMapper objectMapper;

    public ChallangeJmxResource(ChallengeFacade challengeFacade, ObjectMapper objectMapper) {
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
                .subscribe(new ReactorLoggingSubscriber<>("challenge", this.getClass()));
        return "Challenge executed: " + challengeId;
    }

    @ManagedOperation
    public String runChallenge(String challengeId, String teamId) {
        challengeFacade.runChallenge(challengeId, teamId)
                .subscribe(new ReactorLoggingSubscriber<>("challenge for team", this.getClass()));
        return "Challenge executed: " + challengeId + " for team: " + teamId;
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
