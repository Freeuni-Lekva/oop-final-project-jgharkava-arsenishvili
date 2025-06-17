package org.ja.model.OtherObjects;
import java.sql.Timestamp;

public class History {
    private long historyId;
    private long userId;
    private long quizId;
    private long score;
    private double completionTime; // in minutes
    private Timestamp completionDate;

    // Empty constructor
    public History() {
    }

    // Constructor with all parameters
    public History(long historyId, long userId, long quizId, long score, double completionTime, Timestamp completionDate) {
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

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public double getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    public Timestamp getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return historyId == ((History) o).getHistoryId()&&userId==((History) o).getUserId()
                &&quizId==((History) o).getQuizId()&&score==((History) o).getScore()&&completionTime==((History) o).getCompletionTime();
    }
}
