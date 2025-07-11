package modelTests.dataTests;

import org.ja.model.data.Challenge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link Challenge} class.
 */
public class ChallengeTest {

    @Test
    public void testConstructorWithoutId() {
        Challenge challenge = new Challenge(1L, 2L, 3L);

        assertEquals(1L, challenge.getSenderUserId());
        assertEquals(2L, challenge.getRecipientUserId());
        assertEquals(3L, challenge.getQuizId());
        assertEquals(0L, challenge.getChallengeId()); // default long
    }

    @Test
    public void testConstructorWithAllFields() {
        Challenge challenge = new Challenge(100L, 1L, 2L, 3L);

        assertEquals(100L, challenge.getChallengeId());
        assertEquals(1L, challenge.getSenderUserId());
        assertEquals(2L, challenge.getRecipientUserId());
        assertEquals(3L, challenge.getQuizId());
    }

    @Test
    public void testSettersAndGetters() {
        Challenge challenge = new Challenge(0L, 0L, 0L);
        challenge.setChallengeId(10L);
        challenge.setSenderUserId(11L);
        challenge.setRecipientUserId(12L);
        challenge.setQuizId(13L);

        assertEquals(10L, challenge.getChallengeId());
        assertEquals(11L, challenge.getSenderUserId());
        assertEquals(12L, challenge.getRecipientUserId());
        assertEquals(13L, challenge.getQuizId());
    }

    @Test
    public void testEqualsSameObject() {
        Challenge challenge = new Challenge(10L, 1L, 2L, 3L);
        assertEquals(challenge, challenge);
    }

    @Test
    public void testEqualsEqualObjects() {
        Challenge c1 = new Challenge(10L, 1L, 2L, 3L);
        Challenge c2 = new Challenge(10L, 1L, 2L, 3L);
        assertEquals(c1, c2);
    }

    @Test
    public void testEqualsDifferentId() {
        Challenge c1 = new Challenge(10L, 1L, 2L, 3L);
        Challenge c2 = new Challenge(11L, 1L, 2L, 3L);
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEqualsDifferentSender() {
        Challenge c1 = new Challenge(10L, 1L, 2L, 3L);
        Challenge c2 = new Challenge(10L, 99L, 2L, 3L);
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEqualsDifferentRecipient() {
        Challenge c1 = new Challenge(10L, 1L, 2L, 3L);
        Challenge c2 = new Challenge(10L, 1L, 99L, 3L);
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEqualsDifferentQuiz() {
        Challenge c1 = new Challenge(10L, 1L, 2L, 3L);
        Challenge c2 = new Challenge(10L, 1L, 2L, 99L);
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEqualsWithNullAndDifferentType() {
        Challenge challenge = new Challenge(10L, 1L, 2L, 3L);
        assertNotEquals(null, challenge);
        assertNotEquals("not a challenge", challenge);
    }
}
