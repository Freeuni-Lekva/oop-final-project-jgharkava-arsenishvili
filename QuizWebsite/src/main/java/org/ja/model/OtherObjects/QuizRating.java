package org.ja.model.OtherObjects;
public class QuizRating {
    private long quizId;
    private long userId;
    private int rating; // should be between 0 and 5
    private String review;

    // Empty constructor
    public QuizRating() {
    }

    // Constructor with all parameters
    public QuizRating(long quizId, long userId, int rating, String review) {
        this.quizId = quizId;
        this.userId = userId;
        this.rating = rating;
        this.review = review;
    }

    // Getters and Setters

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuizRating)) return false;

        return quizId == ((QuizRating) o).getQuizId() &&
                userId == ((QuizRating) o).getUserId() &&
                rating == ((QuizRating) o).getRating() &&
                review.equals(((QuizRating) o).getReview());
    }
}
