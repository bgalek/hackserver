package pl.allegro.experiments.chi.chiserver.domain.scorer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class Score {
    private double value;

    @JsonCreator
    private Score(@JsonProperty("value") double value) {
        Preconditions.checkArgument(value >= 0 && value <= 1, "Score value out of <0, 1>");
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static Score of(double value) {
        return new Score(value);
    }
}
