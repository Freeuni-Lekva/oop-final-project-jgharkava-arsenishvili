package QuestionTests;

import org.ja.model.quiz.question.Question;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
}

