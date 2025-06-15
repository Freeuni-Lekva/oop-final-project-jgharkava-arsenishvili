package org.ja.model.quiz.question;

import java.util.List;
/*
create table questions(
    question_id bigint primary key auto_increment,
    quiz_id bigint not null,
    question text not null,
    image_url varchar(256) default null,
    question_type enum('question-response', 'fill-in-the-blank', 'multiple-choice', 'picture-response',
       'multi-answer', 'multi-choice-multi-answers', 'matching') not null,

    num_answers int not null default 1,
    order_status enum('unordered', 'ordered') not null default 'ordered',

    check (
        question_type != 'picture-response'
        or image_url is not null
    ),

    check (
        question_type not in ('multi-answer', 'matching')
        or num_answers > 1
    ),

    check (
        question_type != 'multi-answer'
        or order_status = 'unordered'
    ),

    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade
);
 */
public class Question {
    private long questionId;
    private long quizId;
    private String questionText;
    private String imageUrl;
    private String questionType;
    private int numAnswers;
    private String orderStatus;
    /*private List<String> possibleAnswers;
    private int numRequiredAnswers;
    private boolean isOrdered;*/
    public Question(long questionId, long quizId, String questionText,
                    String imageUrl, String questionType, int numAnswers, String orderStatus) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.imageUrl = imageUrl;
        this.questionType = questionType;
        this.numAnswers = numAnswers;
        this.orderStatus = orderStatus;
        this.quizId = quizId;

    }
    public long getQuestionId(){
        return questionId;
    }
    public void setQuestionId(long questionId){
        this.questionId=questionId;
    }
    public long getQuizId(){
        return quizId;
    }
    public void setQuizId(long quizId){
        this.quizId=quizId;
    }
    public String getQuestionText(){return questionText;}
    public void setQuestionText(String questionText){
        this.questionText=questionText;
    }
    public String getImageUrl(){
        return imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl=imageUrl;
    }
    public String getQuestionType(){
        return questionType;
    }
    public void setQuestionType(String questionType){
        this.questionType=questionType;
    }
    public int getNumAnswers(){
        return numAnswers;
    }
    public void setNumAnswers(int numAnswers){
        this.numAnswers=numAnswers;
    }
    public String getOrderStatus(){
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus){
        this.orderStatus=orderStatus;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return questionId == ((Question) o).questionId&&quizId==((Question) o).quizId
                &&questionText==((Question) o).questionText&&imageUrl==((Question) o).imageUrl;
    }
}
