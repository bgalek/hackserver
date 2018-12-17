package pl.allegro.tech.leaders.hackathon.challanges.calc;

import pl.allegro.tech.leaders.hackathon.challanges.Challenge;

import java.util.List;

class Calc implements Challenge {
    @Override
    public String getDescription() {
        return "Your task is to make calculator api!";
    }

    @Override
    public List<String> getExamples() {
        return List.of("1+1", "2+2*2", "(3+3)/3)");
    }
}
