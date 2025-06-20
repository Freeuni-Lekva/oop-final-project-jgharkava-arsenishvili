package org.ja.model.OtherObjects;

import java.util.Objects;

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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Achievement)) return false;

        return achievementName.equals(((Achievement) o).getAchievementName()) &&
                achievementId == ((Achievement) o).getAchievementId() &&
                achievementDescription.equals(((Achievement) o).getAchievementDescription()) &&
                Objects.equals(achievementPhoto, ((Achievement) o).achievementPhoto);
    }
}
