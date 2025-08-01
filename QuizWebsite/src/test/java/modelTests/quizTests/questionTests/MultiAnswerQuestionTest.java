package modelTests.quizTests.questionTests;

import org.ja.model.data.Answer;
import org.ja.model.quiz.question.MultiAnswerQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link MultiAnswerQuestion} class.
 */
public class MultiAnswerQuestionTest {

    @Test
    public void testGradeQuestionCorrectOrdered(){
        MultiAnswerQuestion question = new MultiAnswerQuestion(1, 1, "Name 5 biggest cities in the world",
                null, Constants.QuestionTypes.MULTI_ANSWER_QUESTION, 5, Constants.OrderTypes.ORDERED);

        List<Answer> answers = List.of(new Answer("Tokyo"), new Answer("Delhi"),
                new Answer("Shanghai"), new Answer("Sao Paulo"), new Answer("Mexico city"));
        Response response = new Response();
        response.addAnswer("Tokyo");
        response.addAnswer("Delhi");
        response.addAnswer("Shanghai");
        response.addAnswer("Sao paulo");
        response.addAnswer("MEXICO CITY");

        assertEquals(List.of(1, 1, 1, 1, 1), question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrectOrdered(){
        MultiAnswerQuestion question = new MultiAnswerQuestion(1, 1, "Name 5 biggest cities in the world",
                null, Constants.QuestionTypes.MULTI_ANSWER_QUESTION, 5, Constants.OrderTypes.ORDERED);

        List<Answer> answers = List.of(new Answer("Tokyo"), new Answer("Delhi"),
                new Answer("Shanghai"), new Answer("Sao Paulo¶São Paulo"),
                new Answer("Mexico city¶Mexico"));
        Response response = new Response();
        response.addAnswer("Delhi");
        response.addAnswer("Tokyo");
        response.addAnswer("Shanghai");
        response.addAnswer("Sao paulo");
        response.addAnswer("MEXICO CITY");

        assertEquals(List.of(0, 0, 1, 1, 1), question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionCorrectUnordered(){
        MultiAnswerQuestion question = new MultiAnswerQuestion(1, 1, "Name 5 ancients civilizations",
                null, Constants.QuestionTypes.MULTI_ANSWER_QUESTION, 5, Constants.OrderTypes.UNORDERED);

        List<Answer> answers = List.of(new Answer("Mesopotamian"), new Answer("Egyptian"),
                new Answer("Indus valley¶IVC"), new Answer("Mayan"), new Answer("Greek"));
        Response response = new Response();
        response.addAnswer("mayan");
        response.addAnswer("greek");
        response.addAnswer("indus valley");
        response.addAnswer("MAYAN");
        response.addAnswer("greek ");

        assertEquals(List.of(1, 1, 1, 0, 0), question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrectUnordered(){
        MultiAnswerQuestion question = new MultiAnswerQuestion(1, 1, "Name 5 ancients civilizations",
                null, Constants.QuestionTypes.MULTI_ANSWER_QUESTION, 5, Constants.OrderTypes.UNORDERED);

        List<Answer> answers = List.of(new Answer("Mesopotamian"), new Answer("Egyptian"),
                new Answer("Indus valley¶IVC"), new Answer("Mayan"), new Answer("Greek"));
        Response response = new Response();
        response.addAnswer("roman");
        response.addAnswer("greek");
        response.addAnswer("indus valley");
        response.addAnswer("  american");
        response.addAnswer("greek");

        assertEquals(List.of(0, 1, 1, 0, 0), question.gradeResponse(answers, response));
    }


    @Test
    public void testConstructorWithTextNumAnswersAndOrderStatus() {
        String questionText = "List the planets in the solar system.";
        int numAnswers = 8;
        String orderStatus = Constants.OrderTypes.UNORDERED;

        MultiAnswerQuestion question = new MultiAnswerQuestion(questionText, numAnswers, orderStatus);

        assertEquals(0L, question.getQuizId());
        assertEquals(0L, question.getQuestionId());
        assertEquals(questionText, question.getQuestionText());
        assertNull(question.getImageUrl());
        assertEquals(Constants.QuestionTypes.MULTI_ANSWER_QUESTION, question.getQuestionType());
        assertEquals(numAnswers, question.getNumAnswers());
        assertEquals(orderStatus, question.getOrderStatus());
    }
}
