package modelTests.quizTests.questionTests;

import org.ja.model.data.Match;
import org.ja.model.quiz.question.MatchingQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for the {@link MatchingQuestion} class.
 */
public class MatchingQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        MatchingQuestion question = new MatchingQuestion(1, 1, "Name 5 biggest cities in the world",
                null, Constants.QuestionTypes.MATCHING_QUESTION, 5, Constants.OrderTypes.ORDERED);

        List<Match> matches = List.of(new Match("France", "Paris"),
                new Match("Japan", "Tokyo"),
                new Match("Canada", "Ottawa"),
                new Match("Brazil", "Brasilia"),
                new Match("Egypt", "Cairo")
        );
        Response response = new Response();
        response.addMatch(new Match("Japan", "Tokyo"));
        response.addMatch(new Match("Canada", "Ottawa"));
        response.addMatch(new Match("Brazil", "Brasilia"));
        response.addMatch(new Match("Egypt", "Cairo"));
        response.addMatch(new Match("France", "Paris"));

        assertEquals(List.of(1, 1, 1, 1, 1), question.gradeResponse(matches, response));
    }

    @Test
    public void testGradeQuestionIncorrect1(){
        MatchingQuestion question = new MatchingQuestion(1, 1, "Name 5 biggest cities in the world",
                null, Constants.QuestionTypes.MATCHING_QUESTION, 5, Constants.OrderTypes.ORDERED);

        List<Match> matches = List.of(new Match("France", "Paris"),
                new Match("Japan", "Tokyo"),
                new Match("Canada", "Ottawa"),
                new Match("Brazil", "Brasilia"),
                new Match("Egypt", "Cairo")
        );
        Response response = new Response();
        response.addMatch(new Match("Japan", "Paris"));
        response.addMatch(new Match("Canada", "Ottawa"));
        response.addMatch(new Match("Brazil", "Brasilia"));
        response.addMatch(new Match("Egypt", "Cairo"));
        response.addMatch(new Match("France", "Tokyo"));

        assertEquals(List.of(0, 1, 1, 1, 0), question.gradeResponse(matches, response));
    }

    @Test
    public void testGradeQuestionIncorrect2(){
        MatchingQuestion question = new MatchingQuestion(1, 1, "Name 5 biggest cities in the world",
                null, Constants.QuestionTypes.MATCHING_QUESTION, 5, Constants.OrderTypes.ORDERED);

        List<Match> matches = List.of(new Match("France", "Paris"),
                new Match("Japan", "Tokyo"),
                new Match("Canada", "Ottawa"),
                new Match("Brazil", "Brasilia"),
                new Match("Egypt", "Cairo")
        );
        Response response = new Response();
        response.addMatch(new Match("Japan", "Paris"));
        response.addMatch(new Match("Canada", "Paris"));
        response.addMatch(new Match("Brazil", "Paris"));
        response.addMatch(new Match("Egypt", "Paris"));
        response.addMatch(new Match("France", "Paris"));

        assertEquals(List.of(0, 0, 0, 0, 1), question.gradeResponse(matches, response));
    }


    @Test
    public void testConstructorWithTextAndNumAnswers() {
        String questionText = "Match the countries with their capitals";
        int numAnswers = 4;

        MatchingQuestion question = new MatchingQuestion(questionText, numAnswers);

        assertEquals(0L, question.getQuizId());
        assertEquals(0L, question.getQuestionId());
        assertEquals(questionText, question.getQuestionText());
        assertNull(question.getImageUrl());
        assertEquals(Constants.QuestionTypes.MATCHING_QUESTION, question.getQuestionType());
        assertEquals(numAnswers, question.getNumAnswers());
        assertEquals(Constants.OrderTypes.ORDERED, question.getOrderStatus());
    }

}
