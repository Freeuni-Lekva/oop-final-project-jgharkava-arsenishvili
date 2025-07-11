package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.Friendship;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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


    // --- Mockito Tests ---


    @Test
    public void testInsertFriendRequest_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert error"));

        FriendShipsDao dao = new FriendShipsDao(ds);
        Friendship f = new Friendship(1, 2);

        assertThrows(RuntimeException.class, () -> dao.insertFriendRequest(f));
    }


    @Test
    public void testGetFriendshipByIds_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Select error"));

        FriendShipsDao dao = new FriendShipsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getFriendshipByIds(1, 2));
    }


    @Test
    public void testAcceptFriendRequest_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update error"));

        FriendShipsDao dao = new FriendShipsDao(ds);
        Friendship f = new Friendship(1, 2);

        assertThrows(RuntimeException.class, () -> dao.acceptFriendRequest(f));
    }


    @Test
    public void testRemoveFriendShip_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Delete error"));

        FriendShipsDao dao = new FriendShipsDao(ds);
        Friendship f = new Friendship(1, 2);

        assertThrows(RuntimeException.class, () -> dao.removeFriendShip(f));
    }


    @Test
    public void testGetFriends_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query error"));

        FriendShipsDao dao = new FriendShipsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getFriends(1L, 5));
    }


    @Test
    public void testGetFriendRequests_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query error"));

        FriendShipsDao dao = new FriendShipsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getFriendRequests(1L, 5));
    }
}
