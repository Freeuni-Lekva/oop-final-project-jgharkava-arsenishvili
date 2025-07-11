package modelTests.quizTests;

import org.ja.model.quiz.Quiz;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link Quiz} class.
 */
public class QuizTest {

    @Test
    public void testFullConstructorAndGetters() {
        Timestamp now = Timestamp.from(Instant.now());

        Quiz quiz = new Quiz(
                1L, "Java Basics", "Intro quiz on Java",
                100, 4.5, 250,
                now, 30,
                10L, 20L,
                "ordered", "one-page", "final-correction"
        );

        assertEquals(1L, quiz.getId());
        assertEquals("Java Basics", quiz.getName());
        assertEquals("Intro quiz on Java", quiz.getDescription());
        assertEquals(100, quiz.getScore());
        assertEquals(4.5, quiz.getAvgRating());
        assertEquals(250, quiz.getParticipantCount());
        assertEquals(now, quiz.getCreationDate());
        assertEquals(30, quiz.getTimeInMinutes());
        assertEquals(10L, quiz.getCategoryId());
        assertEquals(20L, quiz.getCreatorId());
        assertEquals("ordered", quiz.getQuestionOrder());
        assertEquals("one-page", quiz.getQuestionPlacement());
        assertEquals("final-correction", quiz.getQuestionCorrection());
    }

    @Test
    public void testMinimalConstructorDefaults() {
        Quiz quiz = new Quiz(
                "Python Quiz", "Basics of Python",
                45, 3L, 4L,
                "randomized", "multiple-page", "immediate-correction"
        );

        assertEquals(0L, quiz.getId());
        assertEquals("Python Quiz", quiz.getName());
        assertEquals("Basics of Python", quiz.getDescription());
        assertEquals(0, quiz.getScore());
        assertEquals(0.0, quiz.getAvgRating());
        assertEquals(0, quiz.getParticipantCount());
        assertNotNull(quiz.getCreationDate());
        assertEquals(45, quiz.getTimeInMinutes());
        assertEquals(3L, quiz.getCategoryId());
        assertEquals(4L, quiz.getCreatorId());
        assertEquals("randomized", quiz.getQuestionOrder());
        assertEquals("multiple-page", quiz.getQuestionPlacement());
        assertEquals("immediate-correction", quiz.getQuestionCorrection());
    }

    @Test
    public void testSetters() {
        Quiz quiz = new Quiz();
        Timestamp now = Timestamp.from(Instant.now());

        quiz.setId(99);
        quiz.setName("Updated Quiz");
        quiz.setDescription("Updated description");
        quiz.setScore(88);
        quiz.setAvgRating(3.8f);
        quiz.setParticipantCount(123);
        quiz.setCreationDate(now);
        quiz.setTimeInMinutes(20);
        quiz.setCategoryId(2L);
        quiz.setCreatorId(3L);
        quiz.setQuestionOrder("ordered");
        quiz.setQuestionPlacement("one-page");
        quiz.setQuestionCorrection("final-correction");

        assertEquals(99, quiz.getId());
        assertEquals("Updated Quiz", quiz.getName());
        assertEquals("Updated description", quiz.getDescription());
        assertEquals(88, quiz.getScore());
        assertEquals(3.8, quiz.getAvgRating(), 0.0001);
        assertEquals(123, quiz.getParticipantCount());
        assertEquals(now, quiz.getCreationDate());
        assertEquals(20, quiz.getTimeInMinutes());
        assertEquals(2L, quiz.getCategoryId());
        assertEquals(3L, quiz.getCreatorId());
        assertEquals("ordered", quiz.getQuestionOrder());
        assertEquals("one-page", quiz.getQuestionPlacement());
        assertEquals("final-correction", quiz.getQuestionCorrection());
    }

    @Test
    public void testEqualsTrue() {
        Timestamp ts = Timestamp.from(Instant.now());

        Quiz q1 = new Quiz(1L, "Q", "D", 10, 3.5, 100, ts, 15, 2L, 3L, "ordered", "one-page", "final-correction");
        Quiz q2 = new Quiz(1L, "Q", "D", 10, 3.5, 100, ts, 15, 2L, 3L, "ordered", "one-page", "final-correction");

        assertEquals(q1, q2);
    }

    @Test
    public void testEqualsFalse_differentFields() {
        Timestamp ts = Timestamp.from(Instant.now());

        Quiz q1 = new Quiz(1L, "Q1", "D", 10, 3.5, 100, ts, 15, 2L, 3L, "ordered", "one-page", "final-correction");
        Quiz q2 = new Quiz(1L, "Q2", "D", 10, 3.5, 100, ts, 15, 2L, 3L, "ordered", "one-page", "final-correction");

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEqualsWithNullAndOtherObject() {
        Quiz quiz = new Quiz();
        assertNotEquals(null, quiz);
        assertNotEquals("Not a quiz", quiz);
    }
}
