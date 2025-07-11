package modelTests.quizTests.questionTests;

import org.ja.model.data.Answer;
import org.ja.model.quiz.question.MultipleChoiceQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


/**
 * Unit tests for the {@link MultipleChoiceQuestion} class.
 */
public class MultipleChoiceQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(1, 1, "What is the capital of Canada?",
                null, Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("Ottawa");
        Response response = new Response();
        response.addAnswer("OTTaWA");

        assertEquals(List.of(1), question.gradeResponse(List.of(answer), response));
    }

    @Test
    public void testGradeQuestionIncorrect(){
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(1, 1, "What is the capital of Canada?",
                null, Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("Ottawa");
        Response response = new Response();
        response.addAnswer("Montreal");

        assertEquals(List.of(0), question.gradeResponse(List.of(answer), response));
    }


    @Test
    public void testConstructor_initializesFieldsCorrectly() {
        String questionText = "What is the capital of Georgia?";
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(questionText);

        assertEquals(0L, question.getQuizId());
        assertEquals(0L, question.getQuestionId());
        assertEquals(questionText, question.getQuestionText());
        assertNull(question.getImageUrl());
        assertEquals(Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, question.getQuestionType());
        assertEquals(1, question.getNumAnswers());
        assertEquals(Constants.OrderTypes.ORDERED, question.getOrderStatus());
    }
}
