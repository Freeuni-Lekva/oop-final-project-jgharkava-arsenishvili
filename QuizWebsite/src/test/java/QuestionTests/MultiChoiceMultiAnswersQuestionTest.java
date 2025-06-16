package QuestionTests;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.question.FillInTheBlankQuestion;
import org.ja.model.quiz.question.MultiChoiceMultiAnswersQuestion;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiChoiceMultiAnswersQuestionTest {

    @Test
    public void testGradeQuestionCorrect(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa/RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("nigeria");
        response.addAnswer("rsa");

        assertEquals(3, question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrect1(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa/RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("nigeria");
        response.addAnswer("rsa");
        response.addAnswer("brazil");

        assertEquals(2, question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrect2(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa/RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("nigeria");
        response.addAnswer("brazil");

        assertEquals(1, question.gradeResponse(answers, response));
    }

    @Test
    public void testGradeQuestionIncorrect3(){
        MultiChoiceMultiAnswersQuestion question = new MultiChoiceMultiAnswersQuestion(1, 1,
                "Which of these following countries are located in Africa?",
                null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                3, Constants.OrderTypes.ORDERED);

        List<Answer> answers =  List.of(new Answer("Nigeria"),
                new Answer("Egypt"), new Answer("South Africa/RSA"));
        Response response = new Response();
        response.addAnswer("egypt");
        response.addAnswer("brazil");

        assertEquals(0, question.gradeResponse(answers, response));
    }
}
