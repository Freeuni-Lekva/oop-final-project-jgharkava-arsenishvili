package modelTests.quizTests.questionTests;

import org.ja.model.data.Answer;
import org.ja.model.quiz.question.MultipleChoiceQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
}
