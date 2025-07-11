package org.ja.model.data;

import java.sql.Timestamp;

/**
 * Represents a quiz completion record (history) for a specific user.
 * Stores the user's score, completion time, and the date when the quiz was completed.
 */
public class History {
    private long historyId;
    private long userId;
    private long quizId;
    private int score;
    private double completionTime; // in minutes
    private Timestamp completionDate;

    /**
     * Constructs an empty History object.
     * Typically used before setting fields manually or populating from a database.
     */
    public History() {}

    /**
     * Constructs a History object with all parameters.
     *
     * @param historyId the unique ID of the history record
     * @param userId the ID of the user who completed the quiz
     * @param quizId the ID of the completed quiz
     * @param score the score obtained by the user
     * @param completionTime the time taken to complete the quiz, in minutes
     * @param completionDate the timestamp when the quiz was completed
     */
    public History(long historyId, long userId, long quizId, int score, double completionTime, Timestamp completionDate) {
        this.historyId = historyId;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.completionTime = completionTime;
        this.completionDate = completionDate;
    }

    /**
     * Constructs a History object without specifying the history ID (e.g., for insert operations).
     *
     * @param userId the ID of the user who completed the quiz
     * @param quizId the ID of the completed quiz
     * @param score the score obtained by the user
     * @param completionTime the time taken to complete the quiz, in minutes
     * @param completionDate the timestamp when the quiz was completed
     */
    public History(long userId, long quizId, int score, double completionTime, Timestamp completionDate) {
        this.historyId = -1;
        this.userId = userId;
        this.quizId = quizId;
        this.score = score;
        this.completionTime = completionTime;
        this.completionDate = completionDate;
    }

    /** @return the unique ID of this history record */
    public long getHistoryId() {
        return historyId;
    }

    /** @param historyId the unique ID to assign to this history */
    public void setHistoryId(long historyId) {
        this.historyId = historyId;
    }

    /** @return the ID of the user who took the quiz */
    public long getUserId() {
        return userId;
    }

    /** @param userId the ID of the user to assign */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /** @return the ID of the quiz that was completed */
    public long getQuizId() {
        return quizId;
    }

    /** @param quizId the ID of the quiz to assign */
    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    /** @return the score obtained by the user */
    public int getScore() {
        return score;
    }

    /** @param score the score to assign */
    public void setScore(int score) {
        this.score = score;
    }

    /** @return the time taken to complete the quiz, in minutes */
    public double getCompletionTime() {
        return completionTime;
    }

    /** @param completionTime the time in minutes to assign */
    public void setCompletionTime(double completionTime) {
        this.completionTime = completionTime;
    }

    /** @return the timestamp when the quiz was completed */
    public Timestamp getCompletionDate() {
        return completionDate;
    }

    /** @param completionDate the timestamp to assign */
    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * Compares this history object to another for equality based on all fields.
     *
     * @param o the object to compare with
     * @return true if all fields are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof History)) return false;

        History other = (History) o;
        return historyId == other.getHistoryId() &&
                userId == other.getUserId() &&
                quizId == other.getQuizId() &&
                score == other.getScore() &&
                completionTime == other.getCompletionTime() &&
                completionDate.equals(other.getCompletionDate());
    }
}
