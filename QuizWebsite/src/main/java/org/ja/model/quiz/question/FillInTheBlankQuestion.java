package org.ja.model.quiz.question;

import org.ja.utils.Constants;

public class FillInTheBlankQuestion extends Question {
    public FillInTheBlankQuestion(long questionId, long quizId, String questionText, String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    public FillInTheBlankQuestion(String questionText){
        super(0L, 0L, questionText, null, Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION,
                1, Constants.OrderTypes.ORDERED);
    }
}
