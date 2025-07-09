package DaoTests;

import org.ja.dao.UsersDao;
import org.ja.model.user.User;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the UsersDao class using an in-memory H2 database.
 */
public class UsersDaoTest extends BaseDaoTest{

    private UsersDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new UsersDao(basicDataSource);
    }

    @Test
    public void testInsertUserAndGetUserById() throws SQLException, NoSuchAlgorithmException {
        User user = new User(-1L, "Dummy", "dddd", "1234", Timestamp.valueOf(LocalDateTime.now()), "image", Constants.UserTypes.USER);

        boolean inserted = dao.insertUser(user);
        assertTrue(inserted);
        assertTrue(user.getId() > 0);
        assertNotNull(user.getRegistrationDate());

        User retrieved = dao.getUserById(user.getId());
        assertNotNull(retrieved);
        assertEquals(user.getUsername(), retrieved.getUsername());
        assertEquals(user.getStatus(), retrieved.getStatus());
    }

    @Test
    public void testGetUserByUsername() throws SQLException, NoSuchAlgorithmException {
        User user = new User(-1L, "Dummy", "dddd", "1234", Timestamp.valueOf(LocalDateTime.now()), "image", Constants.UserTypes.USER);

        boolean inserted = dao.insertUser(user);
        assertTrue(inserted);
        assertTrue(user.getId() > 0);
        assertNotNull(user.getRegistrationDate());

        User retrieved = dao.getUserByUsername(user.getUsername());
        assertNotNull(retrieved);
        assertEquals(user.getUsername(), retrieved.getUsername());
        assertEquals(user.getStatus(), retrieved.getStatus());
    }

    @Test
    public void testRemoveUserById() throws SQLException, NoSuchAlgorithmException {
        User user = new User(-1L, "Dummy", "dddd", "1234", Timestamp.valueOf(LocalDateTime.now()), "image", Constants.UserTypes.USER);

        dao.insertUser(user);
        long id = user.getId();

        boolean removed = dao.removeUserById(id);
        assertTrue(removed);

        User shouldBeNull = dao.getUserById(id);
        assertNull(shouldBeNull);
    }

    @Test
    public void testRemoveUserByName() throws SQLException, NoSuchAlgorithmException {
        User user = new User(-1L, "Dummy", "dddd", "1234", Timestamp.valueOf(LocalDateTime.now()), "image", Constants.UserTypes.USER);

        dao.insertUser(user);
        String name = user.getUsername();

        boolean removed = dao.removeUserByName(name);
        assertTrue(removed);
        assertFalse(dao.removeUserByName(name));
    }

}
