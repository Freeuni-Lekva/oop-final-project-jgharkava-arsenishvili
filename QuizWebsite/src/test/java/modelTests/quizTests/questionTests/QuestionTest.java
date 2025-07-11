package modelTests.quizTests.questionTests;

import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link Question} class.
 */
public class QuestionTest {

    @Test
    void testGettersAndSetters1() {
        Question question = new Question(
                1,
                100,
                "What is the capital of France?",
                null,
                Constants.QuestionTypes.RESPONSE_QUESTION,
                1,
                Constants.OrderTypes.ORDERED
        );

        assertEquals(1, question.getQuestionId());
        assertEquals(100, question.getQuizId());
        assertEquals("What is the capital of France?", question.getQuestionText());
        assertNull(question.getImageUrl());
        assertEquals(Constants.QuestionTypes.RESPONSE_QUESTION, question.getQuestionType());
        assertEquals(1, question.getNumAnswers());
        assertEquals(Constants.OrderTypes.ORDERED, question.getOrderStatus());

        question.setQuestionId(2);
        question.setQuizId(200);
        question.setQuestionText("What is the capital of Georgia?");

        assertEquals(2, question.getQuestionId());
        assertEquals(200, question.getQuizId());
        assertEquals("What is the capital of Georgia?", question.getQuestionText());
    }

    @Test
    void testGettersAndSetters2() {
        Question question = new Question(
                10,
                10,
                null,
                "https://image.url",
                Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION,
                1,
                Constants.OrderTypes.ORDERED
        );

        assertEquals(10, question.getQuestionId());
        assertEquals(10, question.getQuizId());
        assertNull(question.getQuestionText());
        assertEquals("https://image.url", question.getImageUrl());
        assertEquals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION, question.getQuestionType());
        assertEquals(1, question.getNumAnswers());
        assertEquals(Constants.OrderTypes.ORDERED, question.getOrderStatus());

        question.setImageUrl("https://imagenew.url");

        assertEquals("https://imagenew.url", question.getImageUrl());
    }

    @Test
    public void testEquals_sameObject() {
        Question q = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        assertEquals(q, q);
    }

    @Test
    public void testEquals_nullAndDifferentClass() {
        Question q = new Question();
        assertNotEquals(null, q);
        assertNotEquals("not a question", q);
    }

    @Test
    public void testEquals_equalObjects() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertEquals(q1, q2);
        assertEquals(q2, q1);
    }

    @Test
    public void testEquals_differentQuestionText() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 2L, "Different question?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEquals_differentImageUrl() {
        Question q1 = new Question(1L, 2L, "Q1?", "img1.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 2L, "Q1?", "img2.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEquals_differentQuestionId() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(5L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEquals_differentQuizId() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 9L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEquals_differentQuestionType() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.RESPONSE_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEquals_differentNumAnswers() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 5, Constants.OrderTypes.ORDERED);

        assertNotEquals(q1, q2);
    }

    @Test
    public void testEquals_differentOrderStatus() {
        Question q1 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.ORDERED);
        Question q2 = new Question(1L, 2L, "Q1?", "img.png",
                Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 3, Constants.OrderTypes.UNORDERED);

        assertNotEquals(q1, q2);
    }
}

