package org.ja.model.quiz.question;

import org.ja.utils.Constants;

/**
 * Represents a fill-in-the-blank type of quiz question.
 * Extends the base Question class with preset defaults for this question type.
 */
public class FillInTheBlankQuestion extends Question {

    /**
     * Constructs a FillInTheBlankQuestion with all parameters specified.
     *
     * @param questionId the unique ID of the question
     * @param quizId the ID of the quiz this question belongs to
     * @param questionText the text of the question, with blanks to fill
     * @param imageUrl optional image URL for the question
     * @param questionType the question type identifier (should be fill-in-the-blank)
     * @param numAnswers the number of answers expected (typically 1)
     * @param orderStatus the ordering status of the question
     */
    public FillInTheBlankQuestion(long questionId, long quizId, String questionText, String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    /**
     * Constructs a FillInTheBlankQuestion with default values for questionId, quizId, imageUrl,
     * questionType (fill-in-the-blank), numAnswers (1), and orderStatus (ordered).
     *
     * @param questionText the text of the question, with blanks to fill
     */
    public FillInTheBlankQuestion(String questionText){
        super(0L, 0L, questionText, null, Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION,
                1, Constants.OrderTypes.ORDERED);
    }
}
