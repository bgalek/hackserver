package pl.allegro.tech.leaders.hackathon.challenge.calc;

import org.springframework.stereotype.Component;
import pl.allegro.tech.leaders.hackathon.challenge.Challenge;

import java.util.List;

@Component
class Calc implements Challenge {
    @Override
    public String getName() {
        return "Calculator Challange";
    }

    @Override
    public String getDescription() {
        return "Your task is to make calculator api!";
    }

    @Override
    public List<String> getExamples() {
        return List.of("1+1", "2+2*2", "(3+3)/3)");
    }
}
