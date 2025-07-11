package org.ja.model.data;

/**
 * Represents a quiz challenge sent from one user to another.
 * Each challenge includes a unique ID, sender and recipient user IDs,
 * and the ID of the quiz being challenged.
 */
public class Challenge {

    private long challengeId;
    private long senderUserId;
    private long recipientUserId;
    private long quizId;

    /**
     * Constructs a Challenge without an ID (typically for new entries before insertion into the database).
     *
     * @param senderUserId the ID of the user who sends the challenge
     * @param recipientUserId the ID of the user who receives the challenge
     * @param quizId the ID of the quiz being challenged
     */
    public Challenge(long senderUserId, long recipientUserId, long quizId) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.quizId = quizId;
    }

    /**
     * Constructs a Challenge with all fields.
     *
     * @param challengeId the unique ID of the challenge
     * @param senderUserId the ID of the sender
     * @param recipientUserId the ID of the recipient
     * @param quizId the ID of the associated quiz
     */
    public Challenge(long challengeId, long senderUserId, long recipientUserId, long quizId) {
        this.challengeId = challengeId;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.quizId = quizId;
    }

    /**
     * Returns the unique ID of the challenge.
     *
     * @return the challenge ID
     */
    public long getChallengeId() {
        return challengeId;
    }

    /**
     * Sets the unique ID of the challenge.
     *
     * @param challengeId the ID to set
     */
    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

    /**
     * Returns the ID of the user who sent the challenge.
     *
     * @return the sender's user ID
     */
    public long getSenderUserId() {
        return senderUserId;
    }

    /**
     * Sets the ID of the user who sent the challenge.
     *
     * @param senderUserId the sender's user ID
     */
    public void setSenderUserId(long senderUserId) {
        this.senderUserId = senderUserId;
    }

    /**
     * Returns the ID of the user who received the challenge.
     *
     * @return the recipient's user ID
     */
    public long getRecipientUserId() {
        return recipientUserId;
    }

    /**
     * Sets the ID of the user who received the challenge.
     *
     * @param recipientUserId the recipient's user ID
     */
    public void setRecipientUserId(long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    /**
     * Returns the ID of the quiz being challenged.
     *
     * @return the quiz ID
     */
    public long getQuizId() {
        return quizId;
    }

    /**
     * Sets the ID of the quiz being challenged.
     *
     * @param quizId the quiz ID
     */
    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    /**
     * Checks equality based on all fields: challenge ID, sender, recipient, and quiz.
     *
     * @param o the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Challenge)) return false;

        Challenge other = (Challenge) o;
        return challengeId == other.getChallengeId() &&
                senderUserId == other.getSenderUserId() &&
                recipientUserId == other.getRecipientUserId() &&
                quizId == other.getQuizId();
    }
}
