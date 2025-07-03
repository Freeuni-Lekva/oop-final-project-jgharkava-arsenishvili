package org.ja.model.quiz.question;

import org.ja.model.OtherObjects.Answer;
import org.ja.model.quiz.response.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    // TODO response may not be great
    /// returns immutable list
    public List<Integer> gradeResponse (List<?> correctAnswersList, Response response){
        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer){
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            if(response.size() == 0) return List.of(0);
            return (correctAnswers.get(0)).containsAnswer(response.getAnswer(0)) ? List.of(1) : List.of(0);
        }
        return List.of();
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
        return questionText.hashCode();
    }

}
