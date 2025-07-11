package daoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.AdministratorsDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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


    // --- Mockito Tests ---


    @Test
    public void testPromoteToAdministrator_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertTrue(dao.promoteToAdministrator(42L));
    }

    @Test
    public void testPromoteToAdministrator_notFound() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertFalse(dao.promoteToAdministrator(42L));
    }

    @Test
    public void testPromoteToAdministrator_sqlException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("fail"));

        AdministratorsDao dao = new AdministratorsDao(ds);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.promoteToAdministrator(1));
        assertTrue(ex.getMessage().contains("promoting user"));
    }

    @Test
    public void testGetTakenQuizzesCount_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(3);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertEquals(3, dao.getTakenQuizzesCount());
    }

    @Test
    public void testGetTakenQuizzesCount_noResult() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertEquals(0, dao.getTakenQuizzesCount());
    }

    @Test
    public void testGetTakenQuizzesCount_sqlException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertThrows(RuntimeException.class, dao::getTakenQuizzesCount);
    }

    @Test
    public void testRemoveUserById_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertTrue(dao.removeUserById(7));
    }

    @Test
    public void testRemoveUserById_notFound() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertFalse(dao.removeUserById(7));
    }

    @Test
    public void testRemoveUserById_sqlException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("fail"));

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertThrows(RuntimeException.class, () -> dao.removeUserById(7));
    }

    @Test
    public void testGetUserCount_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(12);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertEquals(12, dao.getUserCount());
    }

    @Test
    public void testGetUserCount_sqlException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException());

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertThrows(RuntimeException.class, dao::getUserCount);
    }

    @Test
    public void testClearQuizHistory_success() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(3);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertTrue(dao.clearQuizHistory(4));
    }

    @Test
    public void testClearQuizHistory_notFound() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertFalse(dao.clearQuizHistory(999));
    }

    @Test
    public void testClearQuizHistory_sqlException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("fail"));

        AdministratorsDao dao = new AdministratorsDao(ds);
        assertThrows(RuntimeException.class, () -> dao.clearQuizHistory(1));
    }
}
