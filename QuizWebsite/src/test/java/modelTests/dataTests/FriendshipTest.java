package modelTests.dataTests;

import org.ja.model.data.Friendship;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the {@link Friendship} class.
 */
public class FriendshipTest {

    @Test
    public void testConstructorWithUserIdsOnly() {
        Friendship friendship = new Friendship(1L, 2L);

        assertEquals(1L, friendship.getFirstUserId());
        assertEquals(2L, friendship.getSecondUserId());
        assertNull(friendship.getFriendshipDate());
        assertNull(friendship.getFriendshipStatus());
    }

    @Test
    public void testConstructorWithAllFields() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship friendship = new Friendship(1L, 2L, timestamp, "pending");

        assertEquals(1L, friendship.getFirstUserId());
        assertEquals(2L, friendship.getSecondUserId());
        assertEquals("pending", friendship.getFriendshipStatus());
        assertEquals(timestamp, friendship.getFriendshipDate());
    }

    @Test
    public void testSettersAndGetters() {
        Friendship friendship = new Friendship(0L, 0L);

        friendship.setFirstUserId(10L);
        friendship.setSecondUserId(20L);
        Timestamp date = Timestamp.valueOf("2024-01-01 10:00:00");
        friendship.setFriendshipDate(date);
        friendship.setFriendshipStatus("friends");

        assertEquals(10L, friendship.getFirstUserId());
        assertEquals(20L, friendship.getSecondUserId());
        assertEquals(date, friendship.getFriendshipDate());
        assertEquals("friends", friendship.getFriendshipStatus());
    }

    @Test
    public void testEqualsSameObject() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship friendship = new Friendship(1L, 2L, timestamp, "pending");

        assertEquals(friendship, friendship);
    }

    @Test
    public void testEqualsEqualObjects() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship f1 = new Friendship(1L, 2L, timestamp, "friends");
        Friendship f2 = new Friendship(1L, 2L, timestamp, "friends");

        assertEquals(f1, f2);
    }

    @Test
    public void testEqualsDifferentUserIds() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship f1 = new Friendship(1L, 2L, timestamp, "friends");
        Friendship f2 = new Friendship(2L, 1L, timestamp, "friends");

        assertNotEquals(f1, f2);
    }

    @Test
    public void testEqualsDifferentStatus() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship f1 = new Friendship(1L, 2L, timestamp, "pending");
        Friendship f2 = new Friendship(1L, 2L, timestamp, "friends");

        assertNotEquals(f1, f2);
    }

    @Test
    public void testEqualsDifferentTimestamp() {
        Timestamp t1 = Timestamp.valueOf("2024-01-01 12:00:00");
        Timestamp t2 = Timestamp.valueOf("2024-01-01 12:00:01");
        Friendship f1 = new Friendship(1L, 2L, t1, "friends");
        Friendship f2 = new Friendship(1L, 2L, t2, "friends");

        assertNotEquals(f1, f2);
    }

    @Test
    public void testEqualsWithNullAndOtherTypes() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Friendship friendship = new Friendship(1L, 2L, timestamp, "friends");

        assertNotEquals(null, friendship);
        assertNotEquals("not a friendship", friendship);
    }
}
