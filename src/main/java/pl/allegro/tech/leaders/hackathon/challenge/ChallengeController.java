package pl.allegro.tech.leaders.hackathon.challenge;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/challenges")
class ChallengeController {

    private final List<Challenge> challenges;

    ChallengeController(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @GetMapping
    List<ChallengeResponse> getChallanges() {
        return challenges.stream()
                .map(ChallengeResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/test")
    String test() {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/{id}")
    ChallengeDetailsResponse getChallange(@PathVariable String id) {
        return challenges.stream()
                .filter(challenge -> challenge.getId().equals(id))
                .findAny()
                .map(ChallengeDetailsResponse::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Challenge Not Found"));
    }

    private class ChallengeResponse {
        private final String id;
        private final String name;
        private final String description;

        ChallengeResponse(Challenge challenge) {
            this.id = challenge.getId();
            this.name = challenge.getName();
            this.description = challenge.getDescription();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    private class ChallengeDetailsResponse {

        private final String name;
        private final String description;
        private final List<String> examples;

        ChallengeDetailsResponse(Challenge challenge) {
            this.name = challenge.getName();
            this.description = challenge.getDescription();
            this.examples = challenge.getExamples();
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getExamples() {
            return examples;
        }
    }
}
