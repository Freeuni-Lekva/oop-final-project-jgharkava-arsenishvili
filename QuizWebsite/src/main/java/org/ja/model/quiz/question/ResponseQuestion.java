package org.ja.model.quiz.question;

import org.ja.utils.Constants;

public class ResponseQuestion extends Question {
    public ResponseQuestion(long questionId, long quizId, String questionText, String imageUrl,
                            String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }

    public ResponseQuestion(String questionText){
        super(0L, 0L, questionText, null,
                Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);
    }
}
