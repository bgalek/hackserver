package pl.allegro.experiments.chi.chiserver.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimals {
    public static BigDecimal round2(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_DOWN);
    }

    public static BigDecimal round4(double value) {
        return new BigDecimal(value).setScale(4, RoundingMode.HALF_DOWN);
    }

    public static BigDecimal round2(BigDecimal value) {
        if (value == null) {
            return round2(BigDecimal.ZERO);
        }
        return value.setScale(2, RoundingMode.HALF_DOWN);
    }
}
