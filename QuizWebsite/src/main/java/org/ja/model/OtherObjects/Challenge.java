package org.ja.model.OtherObjects;

public class Challenge {
    private long challengeId;
    private long senderUserId;
    private long recipientUserId;
    private long quizId;

    // Empty constructor
    // ????
    public Challenge(long senderUserId, long recipientUserId, long quizId) {
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.quizId = quizId;
    }

    // Constructor with all parameters
    public Challenge(long challengeId, long senderUserId, long recipientUserId, long quizId) {
        this.challengeId = challengeId;
        this.senderUserId = senderUserId;
        this.recipientUserId = recipientUserId;
        this.quizId = quizId;
    }

    // Getters and Setters

    public long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(long challengeId) {
        this.challengeId = challengeId;
    }

    public long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public long getRecipientUserId() {
        return recipientUserId;
    }

    public void setRecipientUserId(long recipientUserId) {
        this.recipientUserId = recipientUserId;
    }

    public long getQuizId() {
        return quizId;
    }

    public void setQuizId(long quizId) {
        this.quizId = quizId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Challenge)) return false;

        return challengeId == ((Challenge) o).getChallengeId() &&
                senderUserId == ((Challenge) o).getSenderUserId() &&
                recipientUserId == ((Challenge) o).getRecipientUserId() &&
                quizId == ((Challenge) o).getQuizId();
    }
}

