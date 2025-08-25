package jungwoonson.board.hotarticle.utils;

import java.time.Duration;
import org.junit.jupiter.api.Test;

public class TimeCalculatorUtilsTest {

    @Test
    void test() {
        Duration duration = TimeCalculatorUtils.calculateDurationToMidnight();
        System.out.println("duration.getSeconds() / 60 = " + duration.getSeconds() / 60);
    }
}
