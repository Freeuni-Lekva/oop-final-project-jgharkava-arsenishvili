package QuestionTests;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.question.FillInTheBlankQuestion;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FillInTheBlankQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        FillInTheBlankQuestion question = new FillInTheBlankQuestion(1, 1, "Water freezes at ___ degrees Celsius.",
                null, Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("0");
        Response response = new Response();
        response.addAnswer("0");

        assertEquals(List.of(1), question.gradeResponse(List.of(answer), response));

    }

    @Test
    public void testGradeQuestionIncorrect(){
        FillInTheBlankQuestion question = new FillInTheBlankQuestion(1, 1, "Water freezes at ___ degrees Celsius.",
                null, Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("0");
        Response response = new Response();
        response.addAnswer("1");

        assertEquals(List.of(0), question.gradeResponse(List.of(answer), response));
    }
}
