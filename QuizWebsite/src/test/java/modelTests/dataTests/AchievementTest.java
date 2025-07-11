package modelTests.dataTests;

import org.ja.model.data.Achievement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Achievement} class.
 */
public class AchievementTest {

    @Test
    public void testDefaultConstructor() {
        Achievement achievement = new Achievement();
        assertEquals(0, achievement.getAchievementId());
        assertNull(achievement.getAchievementName());
        assertNull(achievement.getAchievementDescription());
        assertNull(achievement.getAchievementPhoto());
    }

    @Test
    public void testFullConstructor() {
        Achievement achievement = new Achievement(1L, "Champion", "Won 10 quizzes", "photo.png");

        assertEquals(1L, achievement.getAchievementId());
        assertEquals("Champion", achievement.getAchievementName());
        assertEquals("Won 10 quizzes", achievement.getAchievementDescription());
        assertEquals("photo.png", achievement.getAchievementPhoto());
    }

    @Test
    public void testSettersAndGetters() {
        Achievement achievement = new Achievement();

        achievement.setAchievementId(2L);
        achievement.setAchievementName("Explorer");
        achievement.setAchievementDescription("Completed 5 different quizzes");
        achievement.setAchievementPhoto("explorer.png");

        assertEquals(2L, achievement.getAchievementId());
        assertEquals("Explorer", achievement.getAchievementName());
        assertEquals("Completed 5 different quizzes", achievement.getAchievementDescription());
        assertEquals("explorer.png", achievement.getAchievementPhoto());
    }

    @Test
    public void testEqualsSameObject() {
        Achievement achievement = new Achievement(3L, "Hero", "Scored 100%", "hero.jpg");
        assertEquals(achievement, achievement); // identity
    }

    @Test
    public void testEqualsEqualObjects() {
        Achievement a1 = new Achievement(4L, "Legend", "Perfect score", "legend.jpg");
        Achievement a2 = new Achievement(4L, "Legend", "Perfect score", "legend.jpg");

        assertEquals(a1, a2); // value-based equality
    }

    @Test
    public void testEqualsDifferentId() {
        Achievement a1 = new Achievement(5L, "Title", "Description", "image.png");
        Achievement a2 = new Achievement(6L, "Title", "Description", "image.png");

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentName() {
        Achievement a1 = new Achievement(1L, "Name1", "Desc", "img.png");
        Achievement a2 = new Achievement(1L, "Name2", "Desc", "img.png");

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentDescription() {
        Achievement a1 = new Achievement(1L, "Name", "Desc1", "img.png");
        Achievement a2 = new Achievement(1L, "Name", "Desc2", "img.png");

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsDifferentPhoto() {
        Achievement a1 = new Achievement(1L, "Name", "Desc", "img1.png");
        Achievement a2 = new Achievement(1L, "Name", "Desc", "img2.png");

        assertNotEquals(a1, a2);
    }

    @Test
    public void testEqualsNullPhotoHandling() {
        Achievement a1 = new Achievement(1L, "Name", "Desc", null);
        Achievement a2 = new Achievement(1L, "Name", "Desc", null);

        assertEquals(a1, a2);
    }

    @Test
    public void testEqualsWithNull() {
        Achievement a1 = new Achievement(1L, "Name", "Desc", "img.png");
        assertNotEquals(null, a1);
    }

    @Test
    public void testEqualsWithDifferentClass() {
        Achievement a1 = new Achievement(1L, "Name", "Desc", "img.png");
        String notAchievement = "Not an achievement";

        assertNotEquals(notAchievement, a1);
    }
}
