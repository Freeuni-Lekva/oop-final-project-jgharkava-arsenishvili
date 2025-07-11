package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.UserAchievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the UserAchievementsDao class using an in-memory H2 database.
 */
public class UserAchievementDaoTest extends BaseDaoTest{

    private UserAchievementsDao dao;


    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new UserAchievementsDao(basicDataSource);
    }


    @Test
    public void testGetUserAchievements(){
        List<UserAchievement> achievements = dao.getUserAchievements(6L, Constants.FETCH_LIMIT);
        assertNotNull(achievements);
        assertEquals(2, achievements.size());

        for (UserAchievement ua : achievements) {
            assertEquals(6L, ua.getUserId());
            assertNotNull(ua.getAchievementDate());
        }
    }


    @Test
    void testGetUserAchievementsNoAchievements() {
        List<UserAchievement> achievements = dao.getUserAchievements(1225, Constants.FETCH_LIMIT);
        assertNotNull(achievements);
        assertTrue(achievements.isEmpty());
    }


    // --- Mockito Tests ---


    @Test
    public void testGetUserAchievements_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        UserAchievementsDao dao = new UserAchievementsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getUserAchievements(1L, 5));
    }


    @Test
    public void testGetUserAchievements_successful() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        // Simulate 2 rows in ResultSet
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getLong("user_id")).thenReturn(1L, 1L);
        when(rs.getLong("achievement_id")).thenReturn(100L, 101L);
        when(rs.getTimestamp("achievement_date")).thenReturn(new java.sql.Timestamp(System.currentTimeMillis()));

        UserAchievementsDao dao = new UserAchievementsDao(ds);

        List<UserAchievement> achievements = dao.getUserAchievements(1L, 5);

        // Basic sanity checks
        assertNotNull(achievements);
        assert achievements.size() == 2;
    }
}
