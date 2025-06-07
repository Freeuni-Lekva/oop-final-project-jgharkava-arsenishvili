package org.ja.model.quiz.question;

import java.util.List;

public abstract class Question {
    private int questionId;
    private String questionText;
    private List<String> possibleAnswers;
    private int numRequiredAnswers;
    private boolean isOrdered;

    public int getQuestionId(){
        return questionId;
    }

    public String getQuestionText(){
        return questionText;
    }


}
