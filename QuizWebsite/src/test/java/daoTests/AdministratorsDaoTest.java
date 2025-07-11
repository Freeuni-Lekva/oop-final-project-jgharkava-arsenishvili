package daoTests;

import org.ja.dao.AdministratorsDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the AdministratorsDao class using an in-memory H2 database.
 */
public class AdministratorsDaoTest extends BaseDaoTest{
    private AdministratorsDao adminsDao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        adminsDao = new AdministratorsDao(basicDataSource);
    }

    @Test
    public void testPromoteToAdministrator() {
        assertTrue(adminsDao.promoteToAdministrator(6));

        assertFalse(adminsDao.promoteToAdministrator(5));     // already admin
        assertFalse(adminsDao.promoteToAdministrator(1225));  // not in DB
    }

    @Test
    public void testGetUserCount() {
        assertEquals(4, adminsDao.getUserCount());
    }

    @Test
    public void testTakenQuizzesCount() {
        assertEquals(3, adminsDao.getTakenQuizzesCount());
    }

    @Test
    public void testRemoveUserById() {
        // User 3 exists
        assertTrue(adminsDao.removeUserById(7));

        // Removing again should fail
        assertFalse(adminsDao.removeUserById(7));
    }

    @Test
    public void testClearQuizHistory() {
        assertTrue(adminsDao.clearQuizHistory(6));
        assertFalse(adminsDao.clearQuizHistory(1225));
    }

    @Test
    public void testRemoveUserByIdNonExistentUser() {
        assertFalse(adminsDao.removeUserById(9999));
    }
}
