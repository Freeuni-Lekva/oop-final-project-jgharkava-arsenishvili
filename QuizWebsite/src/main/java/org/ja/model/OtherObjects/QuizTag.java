package org.ja.model.OtherObjects;

public class QuizTag {
    private long quizId;
    private long tagId;

    // Empty constructor
    public QuizTag() {
    }

    // Constructor with all parameters
    public QuizTag(long quizId, long tagId) {
        this.quizId = quizId;
        this.tagId = tagId;
    }

    // Getters and Setters

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public long getTagId() {
        return tagId;
    }

    public void setTagId(long tagId) {
        this.tagId = tagId;
    }
}
