package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Score {
    private double value;

    @JsonCreator
    private Score(@JsonProperty("value") double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    Score plus(Score score) {
        return Score.of(value + score.getValue());
    }

    public static Score of(double value) {
        return new Score(value);
    }
}
