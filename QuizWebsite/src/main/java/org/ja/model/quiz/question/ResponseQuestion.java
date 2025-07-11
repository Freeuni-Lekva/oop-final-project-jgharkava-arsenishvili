package org.ja.model.quiz.question;

import org.ja.utils.Constants;

/**
 * Represents a question where the user provides a free-text response.
 */
public class ResponseQuestion extends Question {

    /**
     * Constructs a ResponseQuestion with all specified parameters.
     *
     * @param questionId unique ID of the question
     * @param quizId the ID of the quiz this question belongs to
     * @param questionText the text content of the question
     * @param imageUrl optional image URL associated with the question (nullable)
     * @param questionType type identifier for this question
     * @param numAnswers number of answers expected (usually 1)
     * @param orderStatus ordering status of the question
     */
    public ResponseQuestion(long questionId, long quizId, String questionText, String imageUrl,
                            String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a ResponseQuestion with default IDs and no image.
     * Sets the question type to RESPONSE_QUESTION and expects 1 answer.
     *
     * @param questionText the text content of the question
     */
    public ResponseQuestion(String questionText){
        super(0L, 0L, questionText, null,
                Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);
    }
}
