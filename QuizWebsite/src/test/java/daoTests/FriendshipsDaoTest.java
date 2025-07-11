package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.Friendship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the FriendshipsDao class using an in-memory H2 database.
 */
public class FriendshipsDaoTest extends BaseDaoTest{
    private FriendShipsDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new FriendShipsDao(basicDataSource);
    }

    @Test
    public void testInsertFriendRequestNew() {
        Friendship friendship = new Friendship(6, 5); // Gio -> Mariam (not existing in original data)

        boolean inserted = dao.insertFriendRequest(friendship);
        assertTrue(inserted, "Friend request should be inserted");
        assertEquals("pending", friendship.getFriendshipStatus());
        assertNotNull(friendship.getFriendshipDate());
    }

    @Test
    public void testInsertFriendRequestExisting() {
        Friendship existing = new Friendship(5, 6); // Already exists in DB

        assertThrows(RuntimeException.class, () -> dao.insertFriendRequest(existing));
    }

    @Test
    public void testGetFriendshipByIds() {

        // Existing
        Friendship friendship = dao.getFriendshipByIds(5, 6);
        assertNotNull(friendship);
        assertEquals("friends", friendship.getFriendshipStatus());

        // Non-existing
        assertNull(dao.getFriendshipByIds(1, 2));
    }

    @Test
    public void testAcceptFriendRequest() {
        Friendship f = new Friendship(6, 7); // from inserted dataset

        boolean updated = dao.acceptFriendRequest(f);
        assertTrue(updated, "Friendship should be updated to friends");
        assertEquals("friends", f.getFriendshipStatus());
        assertNotNull(f.getFriendshipDate());
    }

    @Test
    public void testRemoveFriendship() {
        Friendship friendship = new Friendship(6, 5);

        boolean removed = dao.removeFriendShip(friendship);
        assertTrue(removed, "Friendship should be removed");

        assertNull(dao.getFriendshipByIds(6, 5));
    }

    @Test
    public void testGetFriends() {
        List<Friendship> friendsOf7 = dao.getFriends(7, Constants.FETCH_LIMIT); // Only (7, 8) friends exist

        assertEquals(1, friendsOf7.size());
        assertEquals("friends", friendsOf7.get(0).getFriendshipStatus());
    }

    @Test
    public void testGetFriendRequests() {
        List<Friendship> requestsFor8 = dao.getFriendRequests(8, Constants.FETCH_LIMIT);
        assertTrue(requestsFor8.stream().anyMatch(f -> f.getFriendshipStatus().equals("pending")));
    }
}
