package org.ja.model.data;

/**
 * Represents a user's rating and optional review for a specific quiz.
 * Each rating includes the quiz ID, user ID, score (0 to 5), and a textual review.
 *
 * <p>This class corresponds to the {@code quiz_ratings} table in the database.</p>
 */
public class QuizRating {
    private long quizId;
    private long userId;
    private int rating; // should be between 0 and 5
    private String review;

    /**
     * Constructs a fully initialized {@code QuizRating} object.
     *
     * @param quizId the ID of the quiz being rated
     * @param userId the ID of the user who rated the quiz
     * @param rating the numerical rating between 0 and 5
     * @param review the optional review message
     * @throws IllegalArgumentException if the rating is not between 0 and 5
     */
    public QuizRating(long quizId, long userId, int rating, String review) {
        this.quizId = quizId;
        this.userId = userId;
        setRating(rating);
        this.review = review;
    }

    /** @return the ID of the quiz being rated */
    public long getQuizId() {
        return quizId;
    }

    /** @param quizId the quiz ID to set */
    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    /** @return the ID of the user who gave the rating */
    public long getUserId() {
        return userId;
    }

    /** @param userId the user ID to set */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return the rating score, expected to be in the range 0 to 5
     */
    public int getRating() {
        return rating;
    }

    /**
     * Sets the quiz rating, must be between 0 and 5 (inclusive).
     *
     * @param rating the rating value to set
     * @throws IllegalArgumentException if the rating is outside the allowed range
     */
    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    /** @return the textual review associated with the rating */
    public String getReview() {
        return review;
    }

    /** @param review the review text to set */
    public void setReview(String review) {
        this.review = review;
    }

    /**
     * Checks equality of two {@code QuizRating} objects based on all fields.
     *
     * @param o the object to compare
     * @return true if all fields are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizRating)) return false;

        QuizRating other = (QuizRating) o;
        return quizId == other.getQuizId() &&
                userId == other.getUserId() &&
                rating == other.getRating() &&
                review.equals(other.getReview());
    }
}
