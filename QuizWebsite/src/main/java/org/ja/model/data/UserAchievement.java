package org.ja.model.data;

import java.sql.Timestamp;

/**
 * Represents an achievement earned by a user at a specific date and time.
 */
public class UserAchievement {
    private long userId;
    private long achievementId;
    private Timestamp achievementDate;

    /**
     * Constructs a UserAchievement with all fields.
     *
     * @param userId          the ID of the user who earned the achievement
     * @param achievementId   the ID of the achievement earned
     * @param achievementDate the date and time when the achievement was earned
     */
    public UserAchievement(long userId, long achievementId, Timestamp achievementDate) {
        this.userId = userId;
        this.achievementId = achievementId;
        this.achievementDate = achievementDate;
    }

    /**
     * Returns the ID of the user who earned the achievement.
     *
     * @return the user ID
     */
    public long getUserId() {
        return userId;
    }

    /**
     * Sets the ID of the user who earned the achievement.
     *
     * @param userId the user ID to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * Returns the ID of the achievement.
     *
     * @return the achievement ID
     */
    public long getAchievementId() {
        return achievementId;
    }

    /**
     * Sets the ID of the achievement.
     *
     * @param achievementId the achievement ID to set
     */
    public void setAchievementId(long achievementId) {
        this.achievementId = achievementId;
    }

    /**
     * Returns the date and time when the achievement was earned.
     *
     * @return the achievement date
     */
    public Timestamp getAchievementDate() {
        return achievementDate;
    }

    /**
     * Sets the date and time when the achievement was earned.
     *
     * @param achievementDate the achievement date to set
     */
    public void setAchievementDate(Timestamp achievementDate) {
        this.achievementDate = achievementDate;
    }

    /**
     * Compares this UserAchievement with another object for equality.
     *
     * @param o the other object to compare
     * @return true if both represent the same user achievement with identical fields
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAchievement)) return false;

        UserAchievement other = (UserAchievement) o;
        return userId == other.userId &&
                achievementId == other.achievementId &&
                achievementDate.equals(other.getAchievementDate());
    }
}
