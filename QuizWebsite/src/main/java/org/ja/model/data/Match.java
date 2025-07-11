package org.ja.model.data;

/**
 * Represents a pair in a matching question.
 * Each Match consists of a left and right item that should be associated with each other,
 * and is linked to a specific question via its ID.
 *
 * <p>This class corresponds to the {@code matches} table in the database.</p>
 */
public class Match {
    private long matchId;
    private long questionId;
    private String leftMatch;
    private String rightMatch;

    /**
     * Default constructor.
     * Useful for frameworks and manual field population.
     */
    public Match() {}

    /**
     * Constructs a Match with only the left and right match values.
     * Typically used for insertion before assigning database-generated IDs.
     *
     * @param leftMatch  the left side of the match pair
     * @param rightMatch the right side of the match pair
     */
    public Match(String leftMatch, String rightMatch) {
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
    }

    /**
     * Constructs a Match with all fields provided.
     *
     * @param matchId    the unique match ID
     * @param questionId the ID of the question this match belongs to
     * @param leftMatch  the left side of the match
     * @param rightMatch the right side of the match
     */
    public Match(long matchId, long questionId, String leftMatch, String rightMatch) {
        this.matchId = matchId;
        this.questionId = questionId;
        this.leftMatch = leftMatch;
        this.rightMatch = rightMatch;
    }

    /**
     * @return the match ID
     */
    public long getMatchId() {
        return matchId;
    }

    /**
     * @param matchId the match ID to set
     */
    public void setMatchId(long matchId) {
        this.matchId = matchId;
    }

    /**
     * @return the ID of the question this match belongs to
     */
    public long getQuestionId() {
        return questionId;
    }

    /**
     * @param questionId the question ID to set
     */
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    /**
     * @return the left side of the match
     */
    public String getLeftMatch() {
        return leftMatch;
    }

    /**
     * @param leftMatch the left match text to set
     */
    public void setLeftMatch(String leftMatch) {
        this.leftMatch = leftMatch;
    }

    /**
     * @return the right side of the match
     */
    public String getRightMatch() {
        return rightMatch;
    }

    /**
     * @param rightMatch the right match text to set
     */
    public void setRightMatch(String rightMatch) {
        this.rightMatch = rightMatch;
    }

    /**
     * Checks equality based on the match content (left and right values).
     * Ignores {@code matchId} and {@code questionId}.
     *
     * @param o the object to compare with
     * @return true if both left and right match values are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Match)) return false;

        Match other = (Match) o;
        return leftMatch.equals(other.getLeftMatch()) &&
                rightMatch.equals(other.getRightMatch());
    }

    /**
     * @return a string representation in the form {leftMatch, rightMatch}
     */
    @Override
    public String toString() {
        return "{" + leftMatch + ", " + rightMatch + "}";
    }
}
