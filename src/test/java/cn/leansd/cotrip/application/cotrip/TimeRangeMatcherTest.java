package cn.leansd.cotrip.application.cotrip;


import cn.leansd.base.types.TimeSpan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeRangeMatcherTest {

    private TimeSpanMatcher matcher;

    @BeforeEach
    public void setup() {
        matcher = new TimeSpanMatcher();
    }

    @Test
    public void testMatchingTimeRanges() {
        TimeSpan timeSpan1 = TimeSpan.builder()
            .start(LocalDateTime.of(2023, 8, 21, 8, 0))
            .end(LocalDateTime.of(2023, 8, 21, 10, 0))
            .build();

        TimeSpan timeSpan2 = TimeSpan.builder()
            .start(LocalDateTime.of(2023, 8, 21, 9, 0))
            .end(LocalDateTime.of(2023, 8, 21, 11, 0))
            .build();

        assertTrue(matcher.match(timeSpan1, timeSpan2), "Time ranges should match as they overlap");
    }

    @Test
    public void testNonMatchingTimeRanges() {
        TimeSpan timeSpan1 = TimeSpan.builder()
            .start(LocalDateTime.of(2023, 8, 21, 8, 0))
            .end(LocalDateTime.of(2023, 8, 21, 10, 0))
            .build();

        TimeSpan timeSpan2 = TimeSpan.builder()
            .start(LocalDateTime.of(2023, 8, 21, 10, 1))
            .end(LocalDateTime.of(2023, 8, 21, 11, 0))
            .build();

        assertFalse(matcher.match(timeSpan1, timeSpan2), "Time ranges should not match as they don't overlap");
    }
}
