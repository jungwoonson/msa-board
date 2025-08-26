package jungwoonson.board.articleread.learning;

import org.junit.jupiter.api.Test;

public class LongToDoubleTest {

    @Test
    void longToDoubleTest() {
        // long은 64비트로 정수
        // double은 64비트로 부동소수점
        // 큰 수 일경우 데이터가 유실될 수 있음
        long longValue = 111_111_111_111_111_111L;
        System.out.println(longValue);
        double doubleValue = longValue;
        System.out.println(doubleValue);
        long longValue2 = (long) doubleValue;
        System.out.println(longValue2);
    }
}
