package modelTests.quizTests.responseTests;

import org.ja.model.data.Match;
import org.ja.model.quiz.response.Response;
import org.ja.model.quiz.response.ResponseBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for the {@link ResponseBuilder} class.
 */
public class ResponseBuilderTest {

    @Test
    public void testBuildResponse(){
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("response_1_1", "banana");
        request.addParameter("response_1_2", "pineapple");
        request.addParameter("response_2_pineapple", "fruit");
        request.addParameter("response_1_3", "strawberry");
        request.addParameter("response_3_1", "USA");
        request.addParameter("response_2_banana", "fruit");
        request.addParameter("response_2_strawberry", "fruit");

        List<Response> responses = ResponseBuilder.buildResponse(request);

        assertEquals("banana", responses.get(0).getAnswer(0));
        assertEquals("pineapple", responses.get(0).getAnswer(1));
        assertEquals("strawberry", responses.get(0).getAnswer(2));
        assertEquals(new Match("banana", "fruit"), responses.get(1).getMatch(0));
        assertEquals(new Match("pineapple", "fruit"), responses.get(1).getMatch(1));
        assertEquals(new Match("strawberry", "fruit"), responses.get(1).getMatch(2));
        assertEquals("USA", responses.get(2).getAnswer(0));
    }
}
