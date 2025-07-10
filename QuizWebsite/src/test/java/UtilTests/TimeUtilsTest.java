package UtilTests;

import org.ja.utils.TimeUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link TimeUtils} class.
 */
public class TimeUtilsTest {

    @Test
    public void testFormatDurationMinutesAndSeconds() {
        assertEquals("8 min 30 sec", TimeUtils.formatDuration(8.5));
        assertEquals("1 min 45 sec", TimeUtils.formatDuration(1.75));
    }

    @Test
    public void testFormatDurationOnlyMinutes() {
        assertEquals("3 min", TimeUtils.formatDuration(3.0));
        assertEquals("10 min", TimeUtils.formatDuration(10.0));
    }

    @Test
    public void testFormatDurationOnlySeconds() {
        assertEquals("30 sec", TimeUtils.formatDuration(0.5));
        assertEquals("1 sec", TimeUtils.formatDuration(0.016));
        assertEquals("0 sec", TimeUtils.formatDuration(0.0));
    }

    @Test
    public void testFormatDurationRounding() {
        assertEquals("3 min", TimeUtils.formatDuration(2.999));
        assertEquals("2 sec", TimeUtils.formatDuration(0.0333));
    }
}
