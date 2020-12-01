package pl.allegro.tech.leaders.hackathon.challenge.samples;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import pl.allegro.tech.leaders.hackathon.challenge.ChallengeDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition;
import pl.allegro.tech.leaders.hackathon.challenge.TaskDefinition.TaskWithFixedResult;
import pl.allegro.tech.leaders.hackathon.challenge.TaskScoring;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
class HostnameChallengeDefinition implements ChallengeDefinition {

    private static final List<TaskWithFixedResult> TASKS = List.of(
            TaskDefinition.withFixedResult("Should return simple hostname",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("https://allegro.pl"))),
                    "allegro.pl",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return simple hostname (but not simplest)",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("https://some_host.org"))),
                    "some_host.org",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return unix file hostname",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("file://localhost/etc/fstab"))),
                    "localhost",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return local file hostname",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("file:///c:/WINDOWS/file.mp3"))),
                    "",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return simple basic-auth hostname",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("http://username:password@example.com/"))),
                    "example.com",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return simple ip based hostname",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("ftp://192.168.0.1"))),
                    "192.168.0.1",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return simple hostname with subdomain",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("http://archiwum.allegro.pl"))),
                    "archiwum.allegro.pl",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return simple hostname with more subdomains",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("https://nowe.archiwum.allegro.pl"))),
                    "nowe.archiwum.allegro.pl",
                    new TaskScoring(5, 1000)),
            TaskDefinition.withFixedResult("Should return simple hostname from url with another url in query",
                    new LinkedMultiValueMap<>(Map.of("url", List.of("https://site.com/?url=https://other.com"))),
                    "site.com",
                    new TaskScoring(5, 1000))
    );

    @Override
    public String getName() {
        return "Hostname Madness";
    }

    @Override
    public String getDescription() {
        return "Your task is to extract hostname from given url, simple as that.";
    }

    @Override
    public String getChallengeEndpoint() {
        return "/hostname";
    }

    @Override
    public Class<String> solutionType() {
        return String.class;
    }

    @Override
    public List<QueryParam> getChallengeParams() {
        return List.of(
                new QueryParam("url", "url to get hostname from")
        );
    }

    @Override
    public TaskDefinition getExample() {
        return TaskDefinition.withFixedResult("Should return wp.pl from https://wp.pl", new LinkedMultiValueMap<>(Map.of("url", List.of("https://wp.pl"))), "wp.pl", new TaskScoring(4, 100));
    }

    @Override
    public List<TaskDefinition> getTasks() {
        return new ArrayList<>(TASKS);
    }
}
