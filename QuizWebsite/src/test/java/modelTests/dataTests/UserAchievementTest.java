package modelTests.dataTests;

import org.ja.model.data.UserAchievement;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link UserAchievement} class.
 */
public class UserAchievementTest {

    @Test
    public void testConstructorAndGetters() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        UserAchievement ua = new UserAchievement(1L, 10L, now);

        assertEquals(1L, ua.getUserId());
        assertEquals(10L, ua.getAchievementId());
        assertEquals(now, ua.getAchievementDate());
    }

    @Test
    public void testSetters() {
        UserAchievement ua = new UserAchievement(0, 0, new Timestamp(0));
        Timestamp now = new Timestamp(System.currentTimeMillis());

        ua.setUserId(5L);
        ua.setAchievementId(20L);
        ua.setAchievementDate(now);

        assertEquals(5L, ua.getUserId());
        assertEquals(20L, ua.getAchievementId());
        assertEquals(now, ua.getAchievementDate());
    }

    @Test
    public void testEqualsReflexive() {
        UserAchievement ua = new UserAchievement(1L, 10L, new Timestamp(0));
        assertEquals(ua, ua);
    }

    @Test
    public void testEqualsSymmetricAndConsistent() {
        Timestamp ts = new Timestamp(0);
        UserAchievement ua1 = new UserAchievement(1L, 10L, ts);
        UserAchievement ua2 = new UserAchievement(1L, 10L, ts);

        assertEquals(ua1, ua2);
        assertEquals(ua2, ua1);
        assertEquals(ua1, ua2);
    }

    @Test
    public void testEqualsNullAndDifferentClass() {
        UserAchievement ua = new UserAchievement(1L, 10L, new Timestamp(0));
        assertNotEquals(null, ua);
        assertNotEquals("not a UserAchievement", ua);
    }

    @Test
    public void testNotEqualsDifferentFields() {
        Timestamp ts1 = new Timestamp(0);
        Timestamp ts2 = new Timestamp(1);

        UserAchievement base = new UserAchievement(1L, 10L, ts1);

        UserAchievement diffUser = new UserAchievement(2L, 10L, ts1);
        UserAchievement diffAchievement = new UserAchievement(1L, 11L, ts1);
        UserAchievement diffDate = new UserAchievement(1L, 10L, ts2);

        assertNotEquals(base, diffUser);
        assertNotEquals(base, diffAchievement);
        assertNotEquals(base, diffDate);
    }
}
