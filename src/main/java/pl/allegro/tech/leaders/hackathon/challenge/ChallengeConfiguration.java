package pl.allegro.tech.leaders.hackathon.challenge;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.allegro.tech.leaders.hackathon.challenge.api.TeamClient;
import pl.allegro.tech.leaders.hackathon.registration.RegistrationFacade;
import pl.allegro.tech.leaders.hackathon.scores.api.ScoreRegistry;

import java.time.Clock;

@Configuration
class ChallengeConfiguration {
    @Bean
    ChallengeFacade challengeFacade(
            Clock clock,
            ObjectMapper objectMapper,
            TeamClient teamClient,
            ChallengeStateRepository challengeStateRepository,
            ChallengeResultRepository challengeResultRepository,
            RegistrationFacade registrationFacade,
            ScoreRegistry scoreRegistry) {
        ChallengeCreator creator = new ChallengeCreator();
        ChallengeRepository repository = new ChallengeRepository(challengeStateRepository, creator);
        ChallengeProvider provider = new ChallengeProvider(repository);
        ChallengeActivator activator = new ChallengeActivator(clock, repository);
        ChallengeRegistrar registrar = new ChallengeRegistrar(repository, creator);
        TaskRunner taskRunner = new TaskRunner(objectMapper, teamClient, clock);
        ChallengeRunner challengeRunner = new ChallengeRunner(provider, registrationFacade, taskRunner, challengeResultRepository);
        ChallengeExampleRunner challengeExampleRunner = new ChallengeExampleRunner(provider, registrationFacade, taskRunner);
        return new ChallengeFacade(
                activator,
                registrar,
                provider,
                challengeRunner,
                challengeExampleRunner,
                challengeResultRepository,
                scoreRegistry);
    }
}
