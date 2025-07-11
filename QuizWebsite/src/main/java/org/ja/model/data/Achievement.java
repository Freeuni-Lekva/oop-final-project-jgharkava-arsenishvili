package org.ja.model.data;

import java.util.Objects;


/**
 * Represents an achievement that a user can earn within the application.
 * Each achievement has an ID, name, description, and an optional photo.
 */
public class Achievement {
    private long achievementId;
    private String achievementName;
    private String achievementDescription;
    private String achievementPhoto;


    /**
     * Default no-argument constructor.
     */
    public Achievement() {}


    /**
     * Constructs an Achievement with all attributes specified.
     *
     * @param achievementId the unique identifier of the achievement
     * @param achievementName the name of the achievement
     * @param achievementDescription a short description of the achievement
     * @param achievementPhoto the URL or path to the achievement's photo
     */
    public Achievement(long achievementId, String achievementName, String achievementDescription, String achievementPhoto) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.achievementPhoto = achievementPhoto;
    }


    /**
     * Returns the unique ID of the achievement.
     *
     * @return the achievement ID
     */
    public long getAchievementId() {
        return achievementId;
    }


    /**
     * Sets the unique ID of the achievement.
     *
     * @param achievementId the ID to set
     */
    public void setAchievementId(long achievementId) {
        this.achievementId = achievementId;
    }


    /**
     * Returns the name of the achievement.
     *
     * @return the achievement name
     */
    public String getAchievementName() {
        return achievementName;
    }


    /**
     * Sets the name of the achievement.
     *
     * @param achievementName the name to set
     */
    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }


    /**
     * Returns the description of the achievement.
     *
     * @return the achievement description
     */
    public String getAchievementDescription() {
        return achievementDescription;
    }


    /**
     * Sets the description of the achievement.
     *
     * @param achievementDescription the description to set
     */
    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }


    /**
     * Returns the photo URL or path associated with the achievement.
     *
     * @return the achievement photo
     */
    public String getAchievementPhoto() {
        return achievementPhoto;
    }


    /**
     * Sets the photo URL or path for the achievement.
     *
     * @param achievementPhoto the photo to set
     */
    public void setAchievementPhoto(String achievementPhoto) {
        this.achievementPhoto = achievementPhoto;
    }


    /**
     * Compares this achievement to another for equality based on
     * ID, name, description, and photo.
     *
     * @param o the object to compare to
     * @return {@code true} if the achievements are equal, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;

        Achievement other = (Achievement) o;

        return achievementId == other.getAchievementId() &&
                achievementName.equals(other.getAchievementName()) &&
                achievementDescription.equals(other.getAchievementDescription()) &&
                Objects.equals(achievementPhoto, other.getAchievementPhoto());
    }
}
