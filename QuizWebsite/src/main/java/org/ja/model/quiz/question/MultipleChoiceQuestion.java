package org.ja.model.quiz.question;

import org.ja.utils.Constants;

/**
 * Represents a multiple-choice question with a single correct answer.
 */
public class MultipleChoiceQuestion extends Question {

    /**
     * Constructs a MultipleChoiceQuestion with all parameters specified.
     *
     * @param questionId unique ID of the question
     * @param quizId the quiz ID this question belongs to
     * @param questionText the text of the question
     * @param imageUrl optional image URL associated with the question
     * @param questionType the type identifier for this question
     * @param numAnswers number of answers expected (usually 1)
     * @param orderStatus the ordering status of the question
     */
    public MultipleChoiceQuestion(long questionId, long quizId, String questionText, String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a MultipleChoiceQuestion with default IDs and no image.
     * Sets the question type to MULTIPLE_CHOICE_QUESTION and expects 1 answer.
     *
     * @param questionText the text of the question
     */
    public MultipleChoiceQuestion(String questionText){
        super(0L, 0L, questionText, null, Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION,
                1, Constants.OrderTypes.ORDERED);
    }
}
