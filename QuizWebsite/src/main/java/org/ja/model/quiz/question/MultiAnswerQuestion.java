package org.ja.model.quiz.question;

import org.ja.model.data.Answer;
import org.ja.model.quiz.response.Response;
import org.ja.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Represents a quiz question type where multiple answers are expected.
 * Supports grading both ordered and unordered responses.
 */
public class MultiAnswerQuestion extends Question {

    /**
     * Constructs a MultiAnswerQuestion with all parameters specified.
     *
     * @param questionId the unique ID of the question
     * @param quizId the ID of the quiz this question belongs to
     * @param questionText the text of the question
     * @param imageUrl optional image URL for the question
     * @param questionType the question type identifier (multi-answer)
     * @param numAnswers number of expected answers
     * @param orderStatus ordering status (ordered or unordered)
     */
    public MultiAnswerQuestion(long questionId, long quizId, String questionText,
                               String imageUrl, String questionType, int numAnswers, String orderStatus){
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a MultiAnswerQuestion with default questionId and quizId,
     * no image, specified questionText and number of answers,
     * multi-answer question type, and order status.
     *
     * @param questionText the text of the question
     * @param numAnswers number of expected answers
     * @param orderStatus ordering status (ordered or unordered)
     */
    public MultiAnswerQuestion(String questionText, int numAnswers, String orderStatus){
        super(0L, 0L, questionText, null,
                Constants.QuestionTypes.MULTI_ANSWER_QUESTION, numAnswers, orderStatus);
    }

    /**
     * Grades the response for this multi-answer question.
     * If ordered, each response is matched in sequence against correct answers.
     * If unordered, each response is checked if it matches any correct answer without duplication.
     *
     * @param correctAnswersList the list of correct Answer objects
     * @param response the Response object containing user answers
     * @return an immutable list of integers representing grades (1 for correct, 0 for incorrect) per answer
     */
    @Override
    public List<Integer> gradeResponse(List<?> correctAnswersList, Response response){
        List<Integer> grades = new ArrayList<>();

        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer) {
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            if (orderStatus.equals(Constants.OrderTypes.ORDERED)) {
                grades = IntStream.range(0, correctAnswers.size())
                        .map(i -> (correctAnswers.get(i)).containsAnswer(response.getAnswer(i)) ? 1 : 0)
                        .boxed()
                        .toList();
            } else {
                Boolean[] unorderedCorrectAnswers = new Boolean[correctAnswers.size()];
                Arrays.fill(unorderedCorrectAnswers, false);

                for (int i = 0; i < response.size(); i++) {
                    String currResponse = response.getAnswer(i);
                    for (int j = 0; j < correctAnswers.size(); j++) {
                        if (correctAnswers.get(j).containsAnswer(currResponse) && !unorderedCorrectAnswers[j]) {
                            unorderedCorrectAnswers[j] = true;
                            grades.add(1);
                            break;
                        }
                    }
                    if(grades.size() == i) grades.add(0);
                }
            }
        }

        return List.copyOf(grades);
    }
}
