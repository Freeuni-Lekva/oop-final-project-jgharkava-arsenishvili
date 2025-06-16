package org.ja.model.quiz.question;

public class FillInTheBlankQuestion extends Question {
    public FillInTheBlankQuestion(long questionId, long quizId, String questionText, String imageUrl, String questionType, int numAnswers, String orderStatus) {
        super(questionId, quizId, questionText, imageUrl, questionType, numAnswers, orderStatus);
    }
}
