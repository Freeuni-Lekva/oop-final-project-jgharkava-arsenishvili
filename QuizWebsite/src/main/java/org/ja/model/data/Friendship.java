package org.ja.model.data;

import java.sql.Timestamp;

/**
 * Represents a friendship between two users.
 * A friendship can have a status of "pending" or "friends", and stores the date it was established.
 */
public class Friendship {

    private long firstUserId;
    private long secondUserId;
    private Timestamp friendshipDate;
    private String friendshipStatus; // should be "pending" or "friends"

    /**
     * Constructs a friendship with just the two user IDs.
     * Typically used when creating a new friendship request.
     *
     * @param firstUserId  the ID of the first user
     * @param secondUserId the ID of the second user
     */
    public Friendship(long firstUserId, long secondUserId) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
    }

    /**
     * Constructs a full friendship record with date and status.
     *
     * @param firstUserId      the ID of the first user
     * @param secondUserId     the ID of the second user
     * @param friendshipDate   the date the friendship was created
     * @param friendshipStatus the status of the friendship ("pending" or "friends")
     */
    public Friendship(long firstUserId, long secondUserId, Timestamp friendshipDate, String friendshipStatus) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.friendshipDate = friendshipDate;
        this.friendshipStatus = friendshipStatus;
    }

    /**
     * Returns the ID of the first user.
     *
     * @return the first user's ID
     */
    public long getFirstUserId() {
        return firstUserId;
    }

    /**
     * Sets the ID of the first user.
     *
     * @param firstUserId the first user's ID
     */
    public void setFirstUserId(long firstUserId) {
        this.firstUserId = firstUserId;
    }

    /**
     * Returns the ID of the second user.
     *
     * @return the second user's ID
     */
    public long getSecondUserId() {
        return secondUserId;
    }

    /**
     * Sets the ID of the second user.
     *
     * @param secondUserId the second user's ID
     */
    public void setSecondUserId(long secondUserId) {
        this.secondUserId = secondUserId;
    }

    /**
     * Returns the date the friendship was established.
     *
     * @return the friendship date
     */
    public Timestamp getFriendshipDate() {
        return friendshipDate;
    }

    /**
     * Sets the date the friendship was established.
     *
     * @param friendshipDate the friendship date
     */
    public void setFriendshipDate(Timestamp friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    /**
     * Returns the status of the friendship.
     *
     * @return the friendship status, typically "pending" or "friends"
     */
    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    /**
     * Sets the status of the friendship.
     *
     * @param friendshipStatus the friendship status ("pending" or "friends")
     */
    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }

    /**
     * Compares this friendship to another for equality based on user IDs, date, and status.
     *
     * @param o the object to compare with
     * @return true if equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;

        Friendship other = (Friendship) o;
        return firstUserId == other.getFirstUserId() &&
                secondUserId == other.getSecondUserId() &&
                friendshipStatus.equals(other.getFriendshipStatus()) &&
                friendshipDate.equals(other.getFriendshipDate());
    }
}
