package modelTests.dataTests;

import org.ja.model.data.QuizRating;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link QuizRating} class.
 */
public class QuizRatingTest {

    @Test
    public void testConstructorAndGetters() {
        QuizRating rating = new QuizRating(10L, 20L, 4, "Great quiz!");
        assertEquals(10L, rating.getQuizId());
        assertEquals(20L, rating.getUserId());
        assertEquals(4, rating.getRating());
        assertEquals("Great quiz!", rating.getReview());
    }

    @Test
    public void testSetters() {
        QuizRating rating = new QuizRating(0L, 0L, 0, null);

        rating.setQuizId(100L);
        rating.setUserId(200L);
        rating.setRating(5);
        rating.setReview("Excellent");

        assertEquals(100L, rating.getQuizId());
        assertEquals(200L, rating.getUserId());
        assertEquals(5, rating.getRating());
        assertEquals("Excellent", rating.getReview());
    }

    @Test
    public void testSetRatingValidValues() {
        QuizRating rating = new QuizRating(0L, 0L, 0, null);
        for (int i = 0; i <= 5; i++) {
            rating.setRating(i);
            assertEquals(i, rating.getRating());
        }
    }

    @Test
    public void testSetRatingInvalidValues() {
        QuizRating rating = new QuizRating(0L, 0L, 0, null);

        assertThrows(IllegalArgumentException.class, () -> rating.setRating(-1));
        assertThrows(IllegalArgumentException.class, () -> rating.setRating(6));
    }

    @Test
    public void testEquals() {
        QuizRating r1 = new QuizRating(1L, 2L, 3, "Good");
        QuizRating r2 = new QuizRating(1L, 2L, 3, "Good");
        QuizRating r3 = new QuizRating(1L, 2L, 4, "Good");
        QuizRating r4 = new QuizRating(1L, 2L, 3, "Bad");

        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertNotEquals(r1, r4);
        assertNotEquals(null, r1);
        assertNotEquals("some string", r1);
    }
}
