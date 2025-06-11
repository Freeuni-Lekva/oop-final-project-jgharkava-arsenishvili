package org.ja.model.OtherObjects;
import java.sql.Timestamp;

public class History {
    private long historyId;
    private long userId;
    private long quizId;
    private double score;
    private long completionTime; // in milliseconds or seconds depending on usage
    private Timestamp completionDate;

    // Empty constructor
    public History() {
    }

    // Constructor with all parameters
    public History(long historyId, long userId, long quizId, double score, long completionTime, Timestamp completionDate) {
        this.historyId = historyId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.completionTime = completionTime;
        this.completionDate = completionDate;
    }

    // Getters and Setters

    public long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public long getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(long completionTime) {
        this.completionTime = completionTime;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }
}
