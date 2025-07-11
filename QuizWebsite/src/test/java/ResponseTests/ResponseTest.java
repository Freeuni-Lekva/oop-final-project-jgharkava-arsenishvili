package ResponseTests;

import org.ja.model.data.Match;
import org.ja.model.quiz.question.FillInTheBlankQuestion;
import org.ja.model.quiz.response.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link Response} class.
 */
public class ResponseTest {

    @Test
    public void testAnswerResponse(){
        Response response = new Response();

        response.addAnswer("Delhi");
        response.addAnswer("mumbai");
        response.addAnswer("jakarta");

        assertEquals(3, response.size());

        assertEquals("Delhi", response.getAnswer(0));
        assertEquals("mumbai", response.getAnswer(1));
        assertEquals("jakarta", response.getAnswer(2));
    }

    @Test
    public void testMatchResponse(){
        Response response = new Response();

        Match match1 = new Match("France", "Paris");
        Match match2 = new Match("Norway", "Oslo");
        Match match3 = new Match("Best team", "ja");

        response.addMatch(match1);
        response.addMatch(match2);
        response.addMatch(match3);

        assertEquals(3, response.size());

        assertEquals(match1, response.getMatch(0));
        assertEquals(match2, response.getMatch(1));
        assertEquals(match3, response.getMatch(2));
    }
}
