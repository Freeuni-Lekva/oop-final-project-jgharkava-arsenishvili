package org.ja.model.quiz.question;

import org.ja.utils.Constants;

/**
 * Represents a question where the user responds based on a picture shown.
 */
public class PictureResponseQuestion extends Question {

    /**
     * Constructs a PictureResponseQuestion with all parameters specified.
     *
     * @param questionId unique ID of the question
     * @param quizId the quiz ID this question belongs to
     * @param questionText the text of the question
     * @param imageUrl the URL of the picture associated with the question
     * @param questionType the type identifier for this question
     * @param numAnswers number of answers expected (usually 1)
     * @param orderStatus the ordering status of the question
     */
    public PictureResponseQuestion(long questionId, long quizId, String questionText, String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a PictureResponseQuestion with default IDs.
     * Sets the question type to PICTURE_RESPONSE_QUESTION and expects 1 answer.
     *
     * @param imageUrl the URL of the picture associated with the question
     * @param questionText the text of the question
     */
    public PictureResponseQuestion(String imageUrl, String questionText){
        super(0L, 0L, questionText, imageUrl, Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION,
                1, Constants.OrderTypes.ORDERED);
    }
}
