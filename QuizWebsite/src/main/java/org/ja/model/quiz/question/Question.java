package org.ja.model.quiz.question;

import org.ja.model.data.Answer;
import org.ja.model.quiz.response.Response;

import java.util.List;
import java.util.Objects;

/**
 * Represents a quiz question with metadata such as text, image, type, and ordering.
 * Provides functionality to grade a response against the correct answers.
 */
public class Question {
    protected long questionId;
    protected long quizId;
    protected String questionText;
    protected String imageUrl;
    protected String questionType;
    protected int numAnswers;
    protected String orderStatus;

    /**
     * Constructs a Question with all required fields.
     *
     * @param questionId the unique ID of the question
     * @param quizId the ID of the quiz this question belongs to
     * @param questionText the text of the question
     * @param imageUrl optional image URL associated with the question
     * @param questionType the type/category of the question (e.g., multiple-choice)
     * @param numAnswers the number of answers expected or allowed
     * @param orderStatus the ordering status (e.g., ordered, randomized)
     */
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

    /**
     * Grades a user response against a list of correct answers.
     *
     * This base implementation only supports grading the first answer for correctness.
     * Subclasses may override this for more complex grading logic.
     *
     * @param correctAnswersList list of correct answers; expected to contain Answer objects
     * @param response the Response object containing user's answers
     * @return an immutable List of integers representing correctness (1 for correct, 0 for incorrect)
     */
    public List<Integer> gradeResponse(List<?> correctAnswersList, Response response){
        if (!correctAnswersList.isEmpty() && correctAnswersList.get(0) instanceof Answer){
            @SuppressWarnings("unchecked")
            List<Answer> correctAnswers = (List<Answer>) correctAnswersList;

            if(response.size() == 0) return List.of(0);
            return (correctAnswers.get(0)).containsAnswer(response.getAnswer(0)) ? List.of(1) : List.of(0);
        }
        return List.of();
    }

    /**
     * Gets the question ID.
     * @return question ID
     */
    public long getQuestionId(){
        return questionId;
    }

    /**
     * Sets the question ID.
     * @param questionId the ID to set
     */
    public void setQuestionId(long questionId){
        this.questionId = questionId;
    }

    /**
     * Gets the quiz ID this question belongs to.
     * @return quiz ID
     */
    public long getQuizId(){
        return quizId;
    }

    /**
     * Sets the quiz ID.
     * @param quizId the ID to set
     */
    public void setQuizId(long quizId){
        this.quizId = quizId;
    }

    /**
     * Gets the question text.
     * @return question text
     */
    public String getQuestionText(){
        return questionText;
    }

    /**
     * Sets the question text.
     * @param questionText the text to set
     */
    public void setQuestionText(String questionText){
        this.questionText = questionText;
    }

    /**
     * Gets the image URL associated with the question.
     * @return image URL
     */
    public String getImageUrl(){
        return imageUrl;
    }

    /**
     * Sets the image URL for the question.
     * @param imageUrl the URL to set
     */
    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    /**
     * Gets the question type.
     * @return question type
     */
    public String getQuestionType(){
        return questionType;
    }

    /**
     * Sets the question type.
     * @param questionType the type to set
     */
    public void setQuestionType(String questionType){
        this.questionType = questionType;
    }

    /**
     * Gets the number of answers expected or allowed.
     * @return number of answers
     */
    public int getNumAnswers(){
        return numAnswers;
    }

    /**
     * Sets the number of answers.
     * @param numAnswers the number to set
     */
    public void setNumAnswers(int numAnswers){
        this.numAnswers = numAnswers;
    }

    /**
     * Gets the question order status (e.g., "ordered" or "randomized").
     * @return order status
     */
    public String getOrderStatus(){
        return orderStatus;
    }

    /**
     * Sets the question order status.
     * @param orderStatus the status to set
     */
    public void setOrderStatus(String orderStatus){
        this.orderStatus = orderStatus;
    }

    /**
     * Checks equality between this and another object.
     * Two Questions are equal if all their fields match.
     *
     * @param o the object to compare
     * @return true if equal; false otherwise
     */
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

    /**
     * Generates a hash code for this question based on question text or image URL.
     *
     * @return hash code
     */
    @Override
    public int hashCode(){
        return questionText != null ? questionText.hashCode() : imageUrl.hashCode();
    }

}
