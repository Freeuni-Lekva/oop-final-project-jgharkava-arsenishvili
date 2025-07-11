package org.ja.model.quiz;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Represents a quiz with metadata such as name, description, scoring,
 * category, creator, question ordering and placement, and correction settings.
 * <p>
 * Corresponds to the "quizzes" table in the database.
 * </p>
 */
public class Quiz {
    private long id;
    private String name;
    private String description;
    private double avgRating;
    private int quizScore;
    private long participantCount;
    private Timestamp creationDate;
    private int timeInMinutes;
    private long categoryId;
    private long creatorId;
    private String questionOrder;
    private String questionPlacement;
    private String questionCorrection;

    /**
     * Default constructor.
     * Creates an empty Quiz instance.
     */
    public Quiz() {}

    /**
     * Creates a Quiz with all fields specified.
     *
     * @param id               the unique quiz ID
     * @param name             the name of the quiz
     * @param description      description of the quiz
     * @param quizScore        total score of the quiz
     * @param avgRating        average rating of the quiz
     * @param participantCount number of participants who took the quiz
     * @param creationDate     timestamp when quiz was created
     * @param timeInMinutes    time limit for the quiz in minutes
     * @param categoryId       ID of the category this quiz belongs to
     * @param creatorId        ID of the user who created the quiz
     * @param questionOrder    question ordering mode ("ordered", "randomized")
     * @param questionPlacement question placement mode ("one-page", "multiple-page")
     * @param questionCorrection correction mode ("immediate-correction", "final-correction")
     */
    public Quiz(long id, String name, String description, int quizScore, double avgRating,
                long participantCount, Timestamp creationDate, int timeInMinutes,
                long categoryId, long creatorId, String questionOrder,
                String questionPlacement, String questionCorrection) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.quizScore = quizScore;
        this.avgRating = avgRating;
        this.participantCount = participantCount;
        this.creationDate = creationDate;
        this.timeInMinutes = timeInMinutes;
        this.categoryId = categoryId;
        this.creatorId = creatorId;
        this.questionOrder = questionOrder;
        this.questionPlacement = questionPlacement;
        this.questionCorrection = questionCorrection;
    }

    /**
     * Creates a new Quiz with minimal parameters.
     * The ID is initialized as 0, creationDate is set to the current time,
     * and quizScore, avgRating, and participantCount are initialized to zero.
     *
     * @param name             the name of the quiz
     * @param description      description of the quiz
     * @param timeInMinutes    time limit for the quiz in minutes
     * @param categoryId       ID of the category
     * @param creatorId        ID of the user who created the quiz
     * @param questionOrder    question ordering mode
     * @param questionPlacement question placement mode
     * @param questionCorrection correction mode
     */
    public Quiz(String name, String description, int timeInMinutes,
                long categoryId, long creatorId, String questionOrder,
                String questionPlacement, String questionCorrection) {
        this.id = 0L;
        this.name = name;
        this.description = description;
        this.quizScore = 0;
        this.avgRating = 0;
        this.participantCount = 0;
        this.creationDate = Timestamp.from(Instant.now());
        this.timeInMinutes = timeInMinutes;
        this.categoryId = categoryId;
        this.creatorId = creatorId;
        this.questionOrder = questionOrder;
        this.questionPlacement = questionPlacement;
        this.questionCorrection = questionCorrection;
    }

    /**
     * Gets the unique quiz ID.
     * @return quiz ID
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the unique quiz ID.
     * @param id quiz ID
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the quiz name.
     * @return quiz name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the quiz name.
     * @param name quiz name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the total quiz score.
     * @return quiz score
     */
    public int getScore() {
        return quizScore;
    }

    /**
     * Sets the total quiz score.
     * @param quizScore quiz score
     */
    public void setScore(int quizScore) {
        this.quizScore = quizScore;
    }

    /**
     * Gets the quiz description.
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the quiz description.
     * @param description description text
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the average rating of the quiz.
     * @return average rating
     */
    public double getAvgRating() {
        return avgRating;
    }

    /**
     * Sets the average rating of the quiz.
     * @param avgRating average rating
     */
    public void setAvgRating(float avgRating) {
        this.avgRating = avgRating;
    }

    /**
     * Gets the number of participants who took the quiz.
     * @return participant count
     */
    public long getParticipantCount() {
        return participantCount;
    }

    /**
     * Sets the number of participants who took the quiz.
     * @param participantCount participant count
     */
    public void setParticipantCount(long participantCount) {
        this.participantCount = participantCount;
    }

    /**
     * Gets the creation date of the quiz.
     * @return creation timestamp
     */
    public Timestamp getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the quiz.
     * @param creationDate creation timestamp
     */
    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Gets the time limit for the quiz in minutes.
     * @return time limit in minutes
     */
    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    /**
     * Sets the time limit for the quiz in minutes.
     * @param timeInMinutes time limit in minutes
     */
    public void setTimeInMinutes(int timeInMinutes) {
        this.timeInMinutes = timeInMinutes;
    }

    /**
     * Gets the ID of the category to which this quiz belongs.
     * @return category ID
     */
    public long getCategoryId() {
        return categoryId;
    }

    /**
     * Sets the category ID for this quiz.
     * @param categoryId category ID
     */
    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * Gets the user ID of the quiz creator.
     * @return creator user ID
     */
    public long getCreatorId() {
        return creatorId;
    }

    /**
     * Sets the user ID of the quiz creator.
     * @param creatorId creator user ID
     */
    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    /**
     * Gets the question ordering mode.
     * Should be either "ordered" or "randomized".
     * @return question order mode
     */
    public String getQuestionOrder() {
        return questionOrder;
    }

    /**
     * Sets the question ordering mode.
     * @param questionOrder "ordered" or "randomized"
     */
    public void setQuestionOrder(String questionOrder) {
        this.questionOrder = questionOrder;
    }

    /**
     * Gets the question placement mode.
     * Should be either "one-page" or "multiple-page".
     * @return question placement mode
     */
    public String getQuestionPlacement() {
        return questionPlacement;
    }

    /**
     * Sets the question placement mode.
     * @param questionPlacement "one-page" or "multiple-page"
     */
    public void setQuestionPlacement(String questionPlacement) {
        this.questionPlacement = questionPlacement;
    }

    /**
     * Gets the question correction mode.
     * Should be either "immediate-correction" or "final-correction".
     * @return question correction mode
     */
    public String getQuestionCorrection() {
        return questionCorrection;
    }

    /**
     * Sets the question correction mode.
     * @param questionCorrection "immediate-correction" or "final-correction"
     */
    public void setQuestionCorrection(String questionCorrection) {
        this.questionCorrection = questionCorrection;
    }

    /**
     * Compares this quiz to another object for equality.
     * All fields are compared.
     *
     * @param o other object to compare
     * @return true if all fields are equal; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quiz)) return false;

        Quiz quiz = (Quiz) o;

        return id == quiz.id &&
                quizScore == quiz.quizScore &&
                Double.compare(quiz.avgRating, avgRating) == 0 &&
                participantCount == quiz.participantCount &&
                timeInMinutes == quiz.timeInMinutes &&
                categoryId == quiz.categoryId &&
                creatorId == quiz.creatorId &&
                name.equals(quiz.name) &&
                description.equals(quiz.description) &&
                creationDate.equals(quiz.creationDate) &&
                questionOrder.equals(quiz.questionOrder) &&
                questionPlacement.equals(quiz.questionPlacement) &&
                questionCorrection.equals(quiz.questionCorrection);
    }
}
