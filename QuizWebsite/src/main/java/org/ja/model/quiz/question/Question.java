package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.OtherObjects.QuizTag;
import org.ja.model.quiz.response.Response;

import java.util.List;
import java.util.Objects;

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
    protected long questionId;
    protected long quizId;
    protected String questionText;
    protected String imageUrl;
    protected String questionType;
    protected int numAnswers;
    protected String orderStatus;

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

    public int gradeResponse (List<?> correctAnswersList, Response response){
        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer){
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            return (correctAnswers.get(0)).containsAnswer(response.getAnswer(0)) ? 1 : 0;
        }
        return 0;
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
    public String getQuestionText(){
        return questionText;
    }
    public void setQuestionText(String questionText){
        this.questionText = questionText;
    }
    public String getImageUrl(){
        return imageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }
    public String getQuestionType(){
        return questionType;
    }
    public void setQuestionType(String questionType){
        this.questionType = questionType;
    }
    public int getNumAnswers(){
        return numAnswers;
    }
    public void setNumAnswers(int numAnswers){
        this.numAnswers = numAnswers;
    }
    public String getOrderStatus(){
        return orderStatus;
    }
    public void setOrderStatus(String orderStatus){
        this.orderStatus = orderStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;

        if (!Objects.equals(questionText, ((Question) o).getQuestionText()) ||
                !Objects.equals(imageUrl, ((Question) o).getImageUrl())) {
            return false;
        }

        return questionId == ((Question) o).questionId &&
                quizId == ((Question) o).quizId &&
                questionType.equals(((Question) o).getQuestionType()) &&
                numAnswers == ((Question) o).getNumAnswers() &&
                orderStatus.equals(((Question) o).getOrderStatus());

    }

    @Override
    public int hashCode(){
        return 1;
    }

}
