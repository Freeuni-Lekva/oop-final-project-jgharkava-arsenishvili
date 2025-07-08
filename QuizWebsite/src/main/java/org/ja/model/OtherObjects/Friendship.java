package org.ja.model.OtherObjects;

import java.sql.Timestamp;

public class Friendship {
    private long firstUserId;
    private long secondUserId;
    private Timestamp friendshipDate;
    private String friendshipStatus; // should be "pending" or "friends"

    // Empty constructor
    public Friendship(long firstUserId, long secondUserId) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
    }

    // Constructor with all parameters
    public Friendship(long firstUserId, long secondUserId, Timestamp friendshipDate, String friendshipStatus) {
        this.firstUserId = firstUserId;
        this.secondUserId = secondUserId;
        this.friendshipDate = friendshipDate;
        this.friendshipStatus = friendshipStatus;
    }

    // Getters and Setters

    public long getFirstUserId() {
        return firstUserId;
    }

    public void setFirstUserId(long firstUserId) {
        this.firstUserId = firstUserId;
    }

    public long getSecondUserId() {
        return secondUserId;
    }

    public void setSecondUserId(long secondUserId) {
        this.secondUserId = secondUserId;
    }

    public Timestamp getFriendshipDate() {
        return friendshipDate;
    }

    public void setFriendshipDate(Timestamp friendshipDate) {
        this.friendshipDate = friendshipDate;
    }

    public String getFriendshipStatus() {
        return friendshipStatus;
    }

    public void setFriendshipStatus(String friendshipStatus) {
        this.friendshipStatus = friendshipStatus;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Friendship)) return false;

        return firstUserId == ((Friendship) o).getFirstUserId() &&
                secondUserId == ((Friendship) o).getSecondUserId() &&
                friendshipStatus.equals(((Friendship) o).getFriendshipStatus()) &&
                friendshipDate.equals(((Friendship) o).getFriendshipDate());
    }
}

