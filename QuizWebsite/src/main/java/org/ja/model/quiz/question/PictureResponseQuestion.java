package org.ja.model.quiz.question;

import org.ja.utils.Constants;

public class PictureResponseQuestion extends Question{
    public PictureResponseQuestion(long questionId, long quizId, String questionText, String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    public PictureResponseQuestion(String imageUrl){
        super(0L, 0L, null, imageUrl, Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION,
                1, Constants.OrderTypes.ORDERED);
    }

}
