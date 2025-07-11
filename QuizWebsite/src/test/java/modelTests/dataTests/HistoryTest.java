package modelTests.dataTests;

import org.ja.model.data.History;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link History} class.
 */
public class HistoryTest {

    @Test
    public void testEmptyConstructorAndSetters() {
        History h = new History();
        h.setHistoryId(1L);
        h.setUserId(10L);
        h.setQuizId(20L);
        h.setScore(85);
        h.setCompletionTime(15.5);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        h.setCompletionDate(now);

        assertEquals(1L, h.getHistoryId());
        assertEquals(10L, h.getUserId());
        assertEquals(20L, h.getQuizId());
        assertEquals(85, h.getScore());
        assertEquals(15.5, h.getCompletionTime());
        assertEquals(now, h.getCompletionDate());
    }

    @Test
    public void testFullConstructor() {
        Timestamp timestamp = Timestamp.valueOf("2025-07-11 10:00:00");
        History h = new History(1L, 10L, 20L, 95, 12.3, timestamp);

        assertEquals(1L, h.getHistoryId());
        assertEquals(10L, h.getUserId());
        assertEquals(20L, h.getQuizId());
        assertEquals(95, h.getScore());
        assertEquals(12.3, h.getCompletionTime());
        assertEquals(timestamp, h.getCompletionDate());
    }

    @Test
    public void testConstructorWithoutHistoryId() {
        Timestamp timestamp = Timestamp.valueOf("2025-07-11 10:00:00");
        History h = new History(10L, 20L, 75, 9.5, timestamp);

        assertEquals(-1L, h.getHistoryId());  // default id for constructor without id
        assertEquals(10L, h.getUserId());
        assertEquals(20L, h.getQuizId());
        assertEquals(75, h.getScore());
        assertEquals(9.5, h.getCompletionTime());
        assertEquals(timestamp, h.getCompletionDate());
    }

    @Test
    public void testEquals() {
        Timestamp timestamp = Timestamp.valueOf("2025-07-11 10:00:00");

        History h1 = new History(1L, 10L, 20L, 85, 15.0, timestamp);
        History h2 = new History(1L, 10L, 20L, 85, 15.0, timestamp);

        assertEquals(h1, h2);
    }

    @Test
    public void testNotEqualsDifferentFields() {
        Timestamp timestamp1 = Timestamp.valueOf("2025-07-11 10:00:00");
        Timestamp timestamp2 = Timestamp.valueOf("2025-07-12 10:00:00");

        History base = new History(1L, 10L, 20L, 85, 15.0, timestamp1);

        History diffId = new History(2L, 10L, 20L, 85, 15.0, timestamp1);
        History diffUser = new History(1L, 11L, 20L, 85, 15.0, timestamp1);
        History diffQuiz = new History(1L, 10L, 21L, 85, 15.0, timestamp1);
        History diffScore = new History(1L, 10L, 20L, 90, 15.0, timestamp1);
        History diffTime = new History(1L, 10L, 20L, 85, 16.0, timestamp1);
        History diffDate = new History(1L, 10L, 20L, 85, 15.0, timestamp2);

        assertNotEquals(base, diffId);
        assertNotEquals(base, diffUser);
        assertNotEquals(base, diffQuiz);
        assertNotEquals(base, diffScore);
        assertNotEquals(base, diffTime);
        assertNotEquals(base, diffDate);
    }

    @Test
    public void testEqualsWithItself() {
        History h = new History();
        assertEquals(h, h);
    }

    @Test
    public void testEqualsWithNullAndOtherObject() {
        History h = new History();
        assertNotEquals(null, h);
        assertNotEquals("some string", h);
    }
}
