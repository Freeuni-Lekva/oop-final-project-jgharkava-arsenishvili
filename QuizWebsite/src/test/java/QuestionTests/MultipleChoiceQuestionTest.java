package QuestionTests;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.question.MultipleChoiceQuestion;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultipleChoiceQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(1, 1, "What is the capital of Canada?",
                null, Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("Ottawa");
        Response response = new Response();
        response.addAnswer("OTTaWA");

        assertEquals(1, question.gradeResponse(List.of(answer), response));
    }

    @Test
    public void testGradeQuestionIncorrect(){
        MultipleChoiceQuestion question = new MultipleChoiceQuestion(1, 1, "What is the capital of Canada?",
                null, Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("Ottawa");
        Response response = new Response();
        response.addAnswer("Montreal");

        assertEquals(0, question.gradeResponse(List.of(answer), response));
    }


}
