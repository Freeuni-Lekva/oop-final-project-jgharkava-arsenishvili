package org.ja.model.OtherObjects;

import java.sql.Timestamp;

public class UserAchievement {
    private long userId;
    private long achievementId;
    private Timestamp achievementDate;

    // Empty constructor
    public UserAchievement() {
    }

    // Constructor with all parameters
    public UserAchievement(long userId, long achievementId, Timestamp achievementDate) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.achievementDate = achievementDate;
    }

    // Getters and Setters

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(long achievementId) {
        this.achievementId = achievementId;
    }

    public Timestamp getAchievementDate() {
        return achievementDate;
    }

    public void setAchievementDate(Timestamp achievementDate) {
        this.achievementDate = achievementDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return userId == ((UserAchievement) o).userId&&achievementId==((UserAchievement) o).achievementId;
    }
}

