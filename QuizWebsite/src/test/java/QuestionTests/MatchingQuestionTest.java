package QuestionTests;

import org.ja.model.OtherObjects.Match;
import org.ja.model.quiz.question.MatchingQuestion;
import org.ja.model.quiz.question.MultiAnswerQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        assertEquals(5, question.gradeResponse(matches, response));
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

        assertEquals(3, question.gradeResponse(matches, response));
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

        assertEquals(1, question.gradeResponse(matches, response));
    }

}
