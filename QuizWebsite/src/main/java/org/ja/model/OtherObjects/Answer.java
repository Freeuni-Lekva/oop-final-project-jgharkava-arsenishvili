package org.ja.model.OtherObjects;

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
    private String answer_text;
    private int answer_order;
    private boolean answer_validity;
    public Answer(){

    }
    public Answer(long answer_id, long questionId, String answer_text, int answer_order, boolean answer_validity){
        this.answerId = answer_id;
        this.questionId = questionId;
        this.answer_text = answer_text;
        this.answer_order = answer_order;
        this.answer_validity = answer_validity;
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
    public String getAnswer_text() {
        return answer_text;
    }
    public void setAnswer_text(String answer_text) {
        this.answer_text = answer_text;
    }
    public int getAnswer_order() {
        return answer_order;
    }
    public void setAnswer_order(int answer_order) {
        this.answer_order = answer_order;
    }
    public boolean getAnswer_validity() {
        return answer_validity;
    }
    public void setAnswer_validity(boolean answer_validity) {
        this.answer_validity = answer_validity;
    }
}
