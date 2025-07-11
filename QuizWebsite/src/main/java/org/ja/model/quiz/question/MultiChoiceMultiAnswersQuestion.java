package org.ja.model.quiz.question;

import org.ja.model.data.Answer;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multiple-choice question where multiple answers can be selected.
 * Each answer is graded individually with 1 for correct and -1 for incorrect.
 */
public class MultiChoiceMultiAnswersQuestion extends Question {

    /**
     * Constructs a MultiChoiceMultiAnswersQuestion with all parameters specified.
     *
     * @param questionId unique ID of the question
     * @param quizId the quiz ID to which this question belongs
     * @param questionText the text of the question
     * @param imageUrl optional URL for an image associated with the question
     * @param questionType the type identifier for this question
     * @param numAnswers expected number of answers
     * @param orderStatus order type (usually ordered)
     */
    public MultiChoiceMultiAnswersQuestion(long questionId, long quizId, String questionText,
                                           String imageUrl, String questionType, int numAnswers, String orderStatus){
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a MultiChoiceMultiAnswersQuestion with default questionId and quizId,
     * no image, and default order status set to ordered.
     *
     * @param questionText the text of the question
     * @param numAnswers expected number of answers
     */
    public MultiChoiceMultiAnswersQuestion(String questionText, int numAnswers){
        super(0L, 0L, questionText, null, Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION,
                numAnswers, Constants.OrderTypes.ORDERED);
    }

    /**
     * Grades the user response for this multi-choice multiple answers question.
     * Each response answer is checked against the list of correct answers that have validity true.
     * Returns a list of grades where 1 means the answer was correct,
     * and -1 means the answer was incorrect.
     *
     * @param correctAnswersList list of Answer objects representing the correct answers
     * @param response Response object containing the user answers
     * @return an immutable list of integer grades (1 for correct, -1 for incorrect) for each response answer
     */
    @Override
    public List<Integer> gradeResponse(List<?> correctAnswersList, Response response){
        List<Integer> grades = new ArrayList<>();

        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer){
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            for (int i = 0; i < response.size(); i++){
                String currResponse = response.getAnswer(i);
                boolean isCorrect = false;

                for (Answer correctAnswer : correctAnswers){
                    if (correctAnswer.getAnswerValidity() && correctAnswer.containsAnswer(currResponse)) {
                        grades.add(1);
                        isCorrect = true;
                        break;
                    }
                }

                if (!isCorrect) grades.add(-1);
            }
        }

        return List.copyOf(grades);
    }
}
