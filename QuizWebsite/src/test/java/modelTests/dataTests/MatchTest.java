package modelTests.dataTests;

import org.ja.model.data.Match;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link Match} class.
 */
public class MatchTest {

    @Test
    public void testDefaultConstructorAndSetters() {
        Match m = new Match();
        m.setMatchId(1L);
        m.setQuestionId(2L);
        m.setLeftMatch("Left");
        m.setRightMatch("Right");

        assertEquals(1L, m.getMatchId());
        assertEquals(2L, m.getQuestionId());
        assertEquals("Left", m.getLeftMatch());
        assertEquals("Right", m.getRightMatch());
    }

    @Test
    public void testConstructorWithLeftRight() {
        Match m = new Match("A", "B");
        assertEquals("A", m.getLeftMatch());
        assertEquals("B", m.getRightMatch());
        // IDs should default to 0 (long default)
        assertEquals(0L, m.getMatchId());
        assertEquals(0L, m.getQuestionId());
    }

    @Test
    public void testFullConstructor() {
        Match m = new Match(10L, 20L, "LeftVal", "RightVal");
        assertEquals(10L, m.getMatchId());
        assertEquals(20L, m.getQuestionId());
        assertEquals("LeftVal", m.getLeftMatch());
        assertEquals("RightVal", m.getRightMatch());
    }

    @Test
    public void testEquals_sameContent() {
        Match m1 = new Match("Left", "Right");
        Match m2 = new Match("Left", "Right");

        assertEquals(m1, m2);
    }

    @Test
    public void testEquals_differentContent() {
        Match m1 = new Match("Left", "Right");
        Match m2 = new Match("Left", "Wrong");

        assertNotEquals(m1, m2);
    }

    @Test
    public void testEquals_ignoresIds() {
        Match m1 = new Match(1L, 2L, "Left", "Right");
        Match m2 = new Match(10L, 20L, "Left", "Right");

        assertEquals(m1, m2);
    }

    @Test
    public void testEquals_nullAndDifferentClass() {
        Match m = new Match("L", "R");
        assertNotEquals(null, m);
        assertNotEquals("not a match", m);
    }

    @Test
    public void testToString() {
        Match m = new Match("Alpha", "Beta");
        assertEquals("{Alpha, Beta}", m.toString());
    }
}
