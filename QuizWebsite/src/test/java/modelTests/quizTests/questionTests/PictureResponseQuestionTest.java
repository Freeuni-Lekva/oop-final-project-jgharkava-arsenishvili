package modelTests.quizTests.questionTests;

import org.ja.model.data.Answer;
import org.ja.model.quiz.question.PictureResponseQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for the {@link PictureResponseQuestion} class.
 */
public class PictureResponseQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        PictureResponseQuestion question = new PictureResponseQuestion(1, 1, null,
                "https://Ottawa.url", Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("Capital city of Canada");
        Response response = new Response();
        response.addAnswer("Capital city of Canada");

        assertEquals(List.of(1), question.gradeResponse(List.of(answer), response));
    }

    @Test
    public void testGradeQuestionIncorrect(){
        PictureResponseQuestion question = new PictureResponseQuestion(1, 1, null,
                "https://Ottawa.url", Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        Answer answer = new Answer("Capital city of Canada");
        Response response = new Response();
        response.addAnswer("Capital city of Germany");

        assertEquals(List.of(0), question.gradeResponse(List.of(answer), response));
    }

    @Test
    public void testConstructorWithImageAndText() {
        String imageUrl = "http://example.com/image.png";
        String questionText = "What is shown in this picture?";

        PictureResponseQuestion question = new PictureResponseQuestion(imageUrl, questionText);

        assertEquals(0L, question.getQuizId());
        assertEquals(0L, question.getQuestionId());
        assertEquals(questionText, question.getQuestionText());
        assertEquals(imageUrl, question.getImageUrl());
        assertEquals(Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION, question.getQuestionType());
        assertEquals(1, question.getNumAnswers());
        assertEquals(Constants.OrderTypes.ORDERED, question.getOrderStatus());
    }
}
