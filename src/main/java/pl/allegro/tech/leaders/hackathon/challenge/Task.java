package pl.allegro.tech.leaders.hackathon.challenge;

public class Task {

    private final String question;
    private final String answer;

    public Task(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    public boolean checkAnswer(String answer) {
        return answer.equals(this.answer);
    }
}
