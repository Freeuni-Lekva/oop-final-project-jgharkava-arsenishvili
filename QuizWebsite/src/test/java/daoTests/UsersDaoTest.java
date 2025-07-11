package daoTests;

import org.ja.dao.UsersDao;
import org.ja.model.data.User;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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


    @Test
    public void testUpdatePhoto() {
        long testUserId = 5L;
        String newPhotoUrl = "https://example.com/new-photo.jpg";

        dao.updatePhoto(newPhotoUrl, testUserId);

        User updatedUser = dao.getUserById(testUserId);
        assertNotNull(updatedUser);
        assertEquals(newPhotoUrl, updatedUser.getPhoto());
    }


    @Test
    public void testInsertUser_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);
        ResultSet rsKeys = mock(ResultSet.class);
        PreparedStatement psDate = mock(PreparedStatement.class);
        ResultSet rsDate = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);

        when(conn.prepareStatement(startsWith("INSERT INTO users"), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(psInsert);
        when(psInsert.executeUpdate()).thenReturn(1);
        when(psInsert.getGeneratedKeys()).thenReturn(rsKeys);
        when(rsKeys.next()).thenReturn(true);
        when(rsKeys.getLong(1)).thenReturn(42L);

        when(conn.prepareStatement(startsWith("SELECT registration_date"))).thenReturn(psDate);
        when(psDate.executeQuery()).thenReturn(rsDate);
        when(rsDate.next()).thenReturn(true);
        when(rsDate.getTimestamp("registration_date")).thenReturn(new Timestamp(System.currentTimeMillis()));

        UsersDao dao = new UsersDao(ds);

        User user = new User();
        user.setUsername("user");
        user.setPasswordHashed("hash");
        user.setPhoto("photo");
        user.setSalt("salt");
        user.setStatus("active");

        boolean result = dao.insertUser(user);

        assertTrue(result);
        assertEquals(42L, user.getId());
        assertNotNull(user.getRegistrationDate());

        verify(psInsert).setString(1, "hash");
        verify(psInsert).setString(2, "user");
        verify(psInsert).setString(3, "photo");
        verify(psInsert).setString(4, "active");
        verify(psInsert).setString(5, "salt");
    }


    @Test
    public void testInsertUser_noGeneratedKey_throws() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement psInsert = mock(PreparedStatement.class);
        ResultSet rsKeys = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), anyInt())).thenReturn(psInsert);
        when(psInsert.executeUpdate()).thenReturn(1);
        when(psInsert.getGeneratedKeys()).thenReturn(rsKeys);
        when(rsKeys.next()).thenReturn(false);  // No keys

        UsersDao dao = new UsersDao(ds);
        User user = new User();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertUser(user));
        assertTrue(ex.getMessage().contains("no ID generated"));
    }


    @Test
    public void testInsertUser_sqlException_throws() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        when(ds.getConnection()).thenThrow(new SQLException("DB failure"));

        UsersDao dao = new UsersDao(ds);
        User user = new User();

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertUser(user));
        assertTrue(ex.getMessage().contains("Error inserting user"));
    }


    @Test
    public void testRemoveUserById_success() throws SQLException {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        UsersDao dao = new UsersDao(ds);
        assertTrue(dao.removeUserById(10L));
    }


    @Test
    public void testRemoveUserById_failure() throws SQLException {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        UsersDao dao = new UsersDao(ds);
        assertFalse(dao.removeUserById(10L));
    }


    @Test
    public void testRemoveUserById_sqlException_throws() throws SQLException {
        BasicDataSource ds = mock(BasicDataSource.class);
        when(ds.getConnection()).thenThrow(new SQLException());

        UsersDao dao = new UsersDao(ds);
        assertThrows(RuntimeException.class, () -> dao.removeUserById(5L));
    }


    @Test
    public void testGetUserById_found() throws SQLException, NoSuchAlgorithmException {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("user_id")).thenReturn(5L);
        when(rs.getString("username")).thenReturn("john");
        when(rs.getString("password_hashed")).thenReturn("hashed");
        when(rs.getString("salt")).thenReturn("salt");
        when(rs.getTimestamp("registration_date")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getString("user_photo")).thenReturn("photo.png");
        when(rs.getString("user_status")).thenReturn("active");

        UsersDao dao = new UsersDao(ds);

        User user = dao.getUserById(5L);

        assertNotNull(user);
        assertEquals("john", user.getUsername());
    }


    @Test
    public void testGetUserById_notFound() throws SQLException {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        UsersDao dao = new UsersDao(ds);

        User user = dao.getUserById(999L);
        assertNull(user);
    }


    @Test
    public void testGetUserByUsername_found() throws SQLException, NoSuchAlgorithmException {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(true);
        when(rs.getLong("user_id")).thenReturn(3L);
        when(rs.getString("username")).thenReturn("alice");
        when(rs.getString("password_hashed")).thenReturn("hashed");
        when(rs.getString("salt")).thenReturn("salt");
        when(rs.getTimestamp("registration_date")).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(rs.getString("user_photo")).thenReturn("photo.png");
        when(rs.getString("user_status")).thenReturn("active");

        UsersDao dao = new UsersDao(ds);

        User user = dao.getUserByUsername("alice");

        assertNotNull(user);
        assertEquals("alice", user.getUsername());
    }


    @Test
    public void testGetUserByUsername_notFound() throws SQLException {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        when(rs.next()).thenReturn(false);

        UsersDao dao = new UsersDao(ds);

        User user = dao.getUserByUsername("unknown");
        assertNull(user);
    }
}
