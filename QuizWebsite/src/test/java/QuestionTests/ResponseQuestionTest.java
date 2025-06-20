package QuestionTests;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.question.ResponseQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResponseQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        Answer answer = new Answer("Tornike/toko/bero");
        ResponseQuestion question = new ResponseQuestion(1, 1, "Who is our team's MVP?",
                null, Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Response response = new Response();
        response.addAnswer("TORNIKE");

        assertEquals(1, question.gradeResponse(List.of(answer), response));
    }

    @Test
    public void testGradeQuestionIncorrect(){
        Answer answer = new Answer("Tornike/toko/bero");
        ResponseQuestion question = new ResponseQuestion(1, 1, "Who is our team's MVP?",
                null, Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Response response = new Response();
        response.addAnswer("minion");

        assertEquals(0, question.gradeResponse(List.of(answer), response));
    }
}
