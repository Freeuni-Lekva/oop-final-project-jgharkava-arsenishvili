package DaoTests;

import org.ja.dao.*;
import org.ja.model.data.UserAchievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the UserAchievementsDao class using an in-memory H2 database.
 */
public class UserAchievementDaoTest extends BaseDaoTest{

    private UserAchievementsDao dao;
    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new UserAchievementsDao(basicDataSource);
    }

    @Test
    public void testGetUserAchievements(){
        List<UserAchievement> achievements = dao.getUserAchievements(6L);
        assertNotNull(achievements);
        assertEquals(2, achievements.size());

        for (UserAchievement ua : achievements) {
            assertEquals(6L, ua.getUserId());
            assertNotNull(ua.getAchievementDate());
        }
    }

    @Test
    void testGetUserAchievementsNoAchievements() {
        List<UserAchievement> achievements = dao.getUserAchievements(1225);
        assertNotNull(achievements);
        assertTrue(achievements.isEmpty());
    }

}
