package org.ja.model.OtherObjects;

public class Achievement {
    private long achievementId;
    private String achievementName;
    private String achievementDescription;
    private String achievementPhoto;

    // Empty constructor
    public Achievement() {}

    // Constructor with all parameters
    public Achievement(long achievementId, String achievementName, String achievementDescription, String achievementPhoto) {
        this.achievementId = achievementId;
        this.achievementName = achievementName;
        this.achievementDescription = achievementDescription;
        this.achievementPhoto = achievementPhoto;
    }

    // Getters and Setters

    public long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(long achievementId) {
        this.achievementId = achievementId;
    }

    public String getAchievementName() {
        return achievementName;
    }

    public void setAchievementName(String achievementName) {
        this.achievementName = achievementName;
    }

    public String getAchievementDescription() {
        return achievementDescription;
    }

    public void setAchievementDescription(String achievementDescription) {
        this.achievementDescription = achievementDescription;
    }

    public String getAchievementPhoto() {
        return achievementPhoto;
    }

    public void setAchievementPhoto(String achievementPhoto) {
        this.achievementPhoto = achievementPhoto;
    }
}
