package DaoTests;

import org.ja.dao.*;
import org.ja.model.OtherObjects.UserAchievement;
import org.ja.utils.Constants;
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
        List<UserAchievement> achievements = dao.getUserAchievements(6L, Constants.FETCH_LIMIT);
        assertNotNull(achievements);
        assertEquals(2, achievements.size());

        for (UserAchievement ua : achievements) {
            assertEquals(6L, ua.getUserId());
            assertNotNull(ua.getAchievementDate());
        }
    }

    @Test
    void testGetUserAchievementsNoAchievements() {
        List<UserAchievement> achievements = dao.getUserAchievements(1225, Constants.FETCH_LIMIT);
        assertNotNull(achievements);
        assertTrue(achievements.isEmpty());
    }

}
