package modelTests.quizTests.questionTests;

import org.ja.model.data.Answer;
import org.ja.model.quiz.question.MultiChoiceMultiAnswersQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for the {@link MultiChoiceMultiAnswersQuestion} class.
 */
public class MultiChoiceMultiAnswersQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa¶RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("nigeria");
        response.addAnswer("rsa");

        assertEquals(List.of(1, 1, 1), question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrect1(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa¶RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("nigeria");
        response.addAnswer("rsa");
        response.addAnswer("brazil");

        assertEquals(List.of(1, 1, 1, -1), question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrect2(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa¶RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("nigeria");
        response.addAnswer("brazil");

        assertEquals(List.of(1, 1, -1), question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrect3(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa¶RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("brazil");

        assertEquals(List.of(1, -1), question.gradeResponse(answers, response));
    }
}
