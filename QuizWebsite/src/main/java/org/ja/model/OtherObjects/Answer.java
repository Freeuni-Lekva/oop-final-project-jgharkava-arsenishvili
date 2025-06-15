package org.ja.model.OtherObjects;

import java.util.Arrays;

public class Answer {
    /*
    create table answers(
        answer_id bigint primary key auto_increment,
        question_id bigint not null,
        answer_text text not null,
        answer_order int not null default 1,
        answer_validity boolean not null default true,

        foreign key (question_id) references questions(question_id) on delete cascade
    );
    */

    private long answerId;
    private long questionId;
    private String answerText;
    private int answerOrder;
    private boolean answerValidity;

    public Answer() {}

    public Answer(long answerId, long questionId, String answerText, int answerOrder, boolean answerValidity) {
        this.answerId = answerId;
        this.questionId = questionId;
        this.answerText = answerText;
        this.answerOrder = answerOrder;
        this.answerValidity = answerValidity;
    }

    public long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(long answerId) {
        this.answerId = answerId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public int getAnswerOrder() {
        return answerOrder;
    }

    public void setAnswerOrder(int answerOrder) {
        this.answerOrder = answerOrder;
    }

    public boolean getAnswerValidity() {
        return answerValidity;
    }

    public void setAnswerValidity(boolean answerValidity) {
        this.answerValidity = answerValidity;
    }

    public boolean containsAnswer(String answer){
        return Arrays.stream(answerText.split("/"))
                .anyMatch(part -> part.equalsIgnoreCase(answer));
    }
}
