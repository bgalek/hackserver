package pl.allegro.tech.leaders.hackathon.challenge.samples;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Test {
    public static final int FIRST_LIKED_NUMBER = 2999;
    public static final int SECOND_LIKED_NUMBER = 1969;
    public static void main(String[] args) {
        System.out.println(solve(new BigDecimal("12341")));
    }

    static boolean solve(BigDecimal number) {
        BigDecimal first = BigDecimal.valueOf(FIRST_LIKED_NUMBER);
        BigDecimal second = BigDecimal.valueOf(SECOND_LIKED_NUMBER);
        for (BigDecimal i = BigDecimal.ONE; i.compareTo(number.divide(first, RoundingMode.DOWN)) < 0; i = i.add(BigDecimal.ONE)) {
            if (number.subtract(i.multiply(first)).remainder(second).equals(BigDecimal.ZERO)) {
                System.out.println(i);
                return true;
            }
        }
        return false;
    }
}
