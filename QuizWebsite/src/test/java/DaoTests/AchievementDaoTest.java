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
    private Achievement a1, a2, a3;

    /**
     * Sets up a fresh in-memory database and test DAO before each test.
     */
    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        executeSqlFile("database/drop.sql");
        executeSqlFile("database/schema.sql");

        dao = new AchievementsDao(basicDataSource);

        // Initializing Achievements
        a1 = new Achievement(0L, "master", "master", "master.jpg");
        a2 = new Achievement(0L, "beginner", "beginner", "beginner.jpg");
        a3 = new Achievement(0L, "grandMaster", "grandMaster", "grandMaster.jpg");
    }

    /**
     * Tests that achievements are inserted correctly and assigned IDs.
     */
    @Test
    public void testInsertAchievement(){
        assertTrue(dao.insertAchievement(a1));
        assertTrue(a1.getAchievementId() > 0);
        assertTrue(dao.insertAchievement(a2));
        assertTrue(dao.insertAchievement(a3));
    }


    /**
     * Tests that duplicate inserts (same name, description, photo) fail gracefully.
     */
    @Test
    public void testInsertDuplicate() {
        assertTrue(dao.insertAchievement(a1));
        Achievement duplicate = new Achievement(0, a1.getAchievementName(), a1.getAchievementDescription(), a1.getAchievementPhoto());

        assertThrows(RuntimeException.class, () -> dao.insertAchievement(duplicate));
    }


    /**
     * Tests retrieval of existing and non-existing achievements by ID.
     */
    @Test
    public void testGetAchievement() {
        assertTrue(dao.insertAchievement(a2));
        long id = a2.getAchievementId();

        Achievement retrieved = dao.getAchievement(id);
        assertNotNull(retrieved);
        assertEquals(a2.getAchievementName(), retrieved.getAchievementName());
        assertEquals(a2.getAchievementDescription(), retrieved.getAchievementDescription());
        assertEquals(a2.getAchievementPhoto(), retrieved.getAchievementPhoto());

        assertNull(dao.getAchievement(1225)); // Should not exist
    }
}
