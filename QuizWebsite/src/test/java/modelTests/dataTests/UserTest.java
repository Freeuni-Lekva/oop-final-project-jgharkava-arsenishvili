package modelTests.dataTests;

import org.ja.model.data.User;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link User} class.
 */
public class UserTest {

    @Test
    public void testConstructorAndGetters() throws Exception {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        User user = new User(1L, "john_doe", "hashedpass", "salt123", now, "/images/john.png", "active");

        assertEquals(1L, user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("hashedpass", user.getPasswordHashed());
        assertEquals("salt123", user.getSalt());
        assertEquals(now, user.getRegistrationDate());
        assertEquals("/images/john.png", user.getPhoto());
        assertEquals("active", user.getStatus());
    }

    @Test
    public void testSettersAndGetters() throws Exception {
        User user = new User(0, "", "", "", new Timestamp(0), null, "");
        Timestamp now = new Timestamp(System.currentTimeMillis());

        user.setId(2L);
        user.setUsername("alice");
        user.setPasswordHashed("newhash");
        user.setSalt("newsalt");
        user.setRegistrationDate(now);
        user.setPhoto("/images/alice.jpg");
        user.setStatus("banned");

        assertEquals(2L, user.getId());
        assertEquals("alice", user.getUsername());
        assertEquals("newhash", user.getPasswordHashed());
        assertEquals("newsalt", user.getSalt());
        assertEquals(now, user.getRegistrationDate());
        assertEquals("/images/alice.jpg", user.getPhoto());
        assertEquals("banned", user.getStatus());
    }

    @Test
    public void testEqualsReflexive() throws Exception {
        User user = new User(1L, "bob", "hash", "salt", new Timestamp(0), null, "active");
        assertEquals(user, user);
    }

    @Test
    public void testEqualsSymmetricAndConsistent() throws Exception {
        Timestamp ts = new Timestamp(0);
        User user1 = new User(1L, "bob", "hash", "salt", ts, "/photo", "active");
        User user2 = new User(1L, "bob", "hash", "salt", ts, "/photo", "active");

        assertEquals(user1, user2);
        assertEquals(user2, user1);

        // Consistency check
        assertEquals(user1, user2);
    }

    @Test
    public void testEqualsNullAndDifferentClass() throws Exception {
        User user = new User(1L, "bob", "hash", "salt", new Timestamp(0), null, "active");
        assertNotEquals(null, user);
        assertNotEquals("some string", user);
    }

    @Test
    public void testNotEqualsDifferentFields() throws Exception {
        Timestamp ts = new Timestamp(0);
        User base = new User(1L, "bob", "hash", "salt", ts, "/photo", "active");

        User diffId = new User(2L, "bob", "hash", "salt", ts, "/photo", "active");
        User diffUsername = new User(1L, "bob2", "hash", "salt", ts, "/photo", "active");
        User diffPassword = new User(1L, "bob", "hash2", "salt", ts, "/photo", "active");
        User diffSalt = new User(1L, "bob", "hash", "salt2", ts, "/photo", "active");
        User diffDate = new User(1L, "bob", "hash", "salt", new Timestamp(1), "/photo", "active");
        User diffPhoto = new User(1L, "bob", "hash", "salt", ts, "/photo2", "active");
        User diffStatus = new User(1L, "bob", "hash", "salt", ts, "/photo", "inactive");

        assertNotEquals(base, diffId);
        assertNotEquals(base, diffUsername);
        assertNotEquals(base, diffPassword);
        assertNotEquals(base, diffSalt);
        assertNotEquals(base, diffDate);
        assertNotEquals(base, diffPhoto);
        assertNotEquals(base, diffStatus);
    }

    @Test
    public void testHashCodeBasedOnUsername() throws Exception {
        User user = new User(1L, "uniqueUser", "pass", "salt", new Timestamp(0), null, "active");
        assertEquals("uniqueUser".hashCode(), user.hashCode());
    }
}
