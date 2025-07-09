package DaoTests;

import org.ja.dao.*;
import org.ja.model.OtherObjects.Achievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AchievementsDao class using an in-memory H2 database.
 */
public class AchievementDaoTest extends BaseDaoTest{
    private AchievementsDao dao;


    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new AchievementsDao(basicDataSource);
    }

    @Test
    public void testGetExistingAchievementById() {
        Achievement a = dao.getAchievement(1L);
        assertNotNull(a);
        assertEquals("Brainstormer", a.getAchievementName());
        assertEquals("Finish 5 quizzes with above 80%", a.getAchievementDescription());
        assertNull(a.getAchievementPhoto());
    }

    @Test
    public void testGetNonExistentAchievement() {
        assertNull(dao.getAchievement(1225)); // ID not present
    }

    @Test
    public void testInsertAchievement() {
        Achievement newAchievement = new Achievement(0L, "Trivia Titan", "Answer 100 questions correctly", null);

        boolean inserted = dao.insertAchievement(newAchievement);
        assertTrue(inserted);

        long id = newAchievement.getAchievementId();
        assertTrue(id > 0, "ID should be auto-generated");

        Achievement fetched = dao.getAchievement(id);
        assertNotNull(fetched);
        assertEquals("Trivia Titan", fetched.getAchievementName());
        assertEquals("Answer 100 questions correctly", fetched.getAchievementDescription());
        assertNull(fetched.getAchievementPhoto());
    }

    @Test
    public void testInsertAchievementWithInvalidDataThrows() {
        Achievement invalid = new Achievement(0L, null, "desc", null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAchievement(invalid));
        assertTrue(ex.getMessage().contains("Error inserting achievement"));
    }

}
