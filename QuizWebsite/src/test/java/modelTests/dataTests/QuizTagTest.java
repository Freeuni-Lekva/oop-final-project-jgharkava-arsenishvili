package modelTests.dataTests;

import org.ja.model.data.QuizTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link QuizTag} class.
 */
public class QuizTagTest {

    @Test
    public void testConstructorAndGetters() {
        QuizTag qt = new QuizTag(10L, 20L);
        assertEquals(10L, qt.getQuizId());
        assertEquals(20L, qt.getTagId());
    }

    @Test
    public void testSetters() {
        QuizTag qt = new QuizTag(1L, 2L);
        qt.setQuizId(100L);
        qt.setTagId(200L);
        assertEquals(100L, qt.getQuizId());
        assertEquals(200L, qt.getTagId());
    }

    @Test
    public void testEqualsSameObject() {
        QuizTag qt = new QuizTag(5L, 6L);
        assertEquals(qt, qt);  // reflexive
    }

    @Test
    public void testEqualsEqualObjects() {
        QuizTag qt1 = new QuizTag(5L, 6L);
        QuizTag qt2 = new QuizTag(5L, 6L);
        assertEquals(qt1, qt2);
        assertEquals(qt2, qt1);  // symmetric
    }

    @Test
    public void testEqualsDifferentObjects() {
        QuizTag qt1 = new QuizTag(5L, 6L);
        QuizTag qt2 = new QuizTag(7L, 6L);
        QuizTag qt3 = new QuizTag(5L, 8L);
        assertNotEquals(qt1, qt2);
        assertNotEquals(qt1, qt3);
    }

    @Test
    public void testEqualsNullAndDifferentClass() {
        QuizTag qt = new QuizTag(5L, 6L);
        assertNotEquals(null, qt);
        assertNotEquals("some string", qt);
    }
}