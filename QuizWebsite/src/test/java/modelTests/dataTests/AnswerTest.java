package modelTests.dataTests;

import org.ja.model.data.Answer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Answer} class.
 */
public class AnswerTest {

    @Test
    public void testDefaultConstructor() {
        Answer answer = new Answer();
        assertEquals(0, answer.getAnswerId());
        assertEquals(0, answer.getQuestionId());
        assertNull(answer.getAnswerText());
        assertEquals(0, answer.getAnswerOrder());
        assertFalse(answer.getAnswerValidity()); // default boolean is false
    }

    @Test
    public void testFullConstructor() {
        Answer answer = new Answer(1L, 10L, "Yes", 2, true);

        assertEquals(1L, answer.getAnswerId());
        assertEquals(10L, answer.getQuestionId());
        assertEquals("Yes", answer.getAnswerText());
        assertEquals(2, answer.getAnswerOrder());
        assertTrue(answer.getAnswerValidity());
    }

    @Test
    public void testSingleTextConstructor() {
        Answer answer = new Answer("Correct");

        assertEquals("Correct", answer.getAnswerText());
        assertEquals(1, answer.getAnswerOrder());
        assertTrue(answer.getAnswerValidity());
    }

    @Test
    public void testTextOrderValidityConstructor() {
        Answer answer = new Answer("Option A", 5, false);

        assertEquals("Option A", answer.getAnswerText());
        assertEquals(5, answer.getAnswerOrder());
        assertFalse(answer.getAnswerValidity());
    }

    @Test
    public void testSettersAndGetters() {
        Answer answer = new Answer();

        answer.setAnswerId(100L);
        answer.setQuestionId(200L);
        answer.setAnswerText("Choice");
        answer.setAnswerOrder(3);
        answer.setAnswerValidity(true);

        assertEquals(100L, answer.getAnswerId());
        assertEquals(200L, answer.getQuestionId());
        assertEquals("Choice", answer.getAnswerText());
        assertEquals(3, answer.getAnswerOrder());
        assertTrue(answer.getAnswerValidity());
    }

    @Test
    public void testEqualsSameObject() {
        Answer a = new Answer("A");
        a.setQuestionId(10);
        assertEquals(a, a);
    }

    @Test
    public void testEqualsEqualValues() {
        Answer a1 = new Answer("True");
        Answer a2 = new Answer("True");
        a1.setQuestionId(5);
        a2.setQuestionId(5);

        assertEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentText() {
        Answer a1 = new Answer("Yes");
        Answer a2 = new Answer("No");
        a1.setQuestionId(10);
        a2.setQuestionId(10);

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentQuestionId() {
        Answer a1 = new Answer("Same");
        Answer a2 = new Answer("Same");
        a1.setQuestionId(1);
        a2.setQuestionId(2);

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsNullAndClassMismatch() {
        Answer a = new Answer("Answer");
        a.setQuestionId(1);

        assertNotEquals(null, a);
        assertNotEquals("string", a);
    }

    @Test
    public void testContainsAnswerSingleWord() {
        Answer a = new Answer("True");
        assertTrue(a.containsAnswer("true"));
        assertTrue(a.containsAnswer(" TRUE  "));
        assertFalse(a.containsAnswer("false"));
    }

    @Test
    public void testContainsAnswerWithMultipleParts() {
        Answer a = new Answer("Yes¶No¶Maybe");

        assertTrue(a.containsAnswer("yes"));
        assertTrue(a.containsAnswer("NO"));
        assertTrue(a.containsAnswer("maybe"));
        assertFalse(a.containsAnswer("probably"));
    }

    @Test
    public void testContainsAnswerEdgeCases() {
        Answer a = new Answer("  Apple ¶ Banana¶Carrot ");
        assertTrue(a.containsAnswer("apple"));
        assertTrue(a.containsAnswer("carrot"));
        assertFalse(a.containsAnswer("tomato"));
    }

    @Test
    public void testToStringReturnsText() {
        Answer a = new Answer("1225");
        assertEquals("1225", a.toString());
    }
}
