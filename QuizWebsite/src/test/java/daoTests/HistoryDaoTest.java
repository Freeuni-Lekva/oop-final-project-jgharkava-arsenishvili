package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.time.LocalDateTime;
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
 * Unit tests for the HistoryDao class using an in-memory H2 database.
 */
public class HistoryDaoTest extends BaseDaoTest{
    private HistoriesDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new HistoriesDao(basicDataSource);
    }

    @Test
    public void testInsertAndRetrieveHistory() throws SQLException {
        History history = new History(5L, 4L, 5, 12.5, Timestamp.valueOf(LocalDateTime.now()));

        boolean inserted = dao.insertHistory(history);
        assertTrue(inserted);
        assertTrue(history.getHistoryId() > 0);
        assertNotNull(history.getCompletionDate());

        List<History> userHistories = dao.getHistoriesByUserId(5L, Constants.FETCH_LIMIT);
        assertFalse(userHistories.isEmpty());
        assertTrue(userHistories.stream().anyMatch(h -> h.getHistoryId() == history.getHistoryId()));
    }

    @Test
    public void testGetHistoriesByQuizId() {
        List<History> histories = dao.getHistoriesByQuizId(6L, Constants.FETCH_LIMIT);
        assertFalse(histories.isEmpty());

        // Check whether descending by date
        for (int i = 1; i < histories.size(); i++)
            assertTrue(histories.get(i - 1).getCompletionDate().after(histories.get(i).getCompletionDate()));
    }

    @Test
    public void testGetTopNDistinctHistoriesByQuizId() {
        List<History> topHistories = dao.getTopNDistinctHistoriesByQuizId(6L, 5);
        assertNotNull(topHistories);
        assertTrue(topHistories.size() <= 5);

        // Ensure distinct users (userId) among results
        long distinctUsers = topHistories.stream().map(History::getUserId).distinct().count();
        assertEquals(topHistories.size(), distinctUsers);
    }

    @Test
    public void testDistinctTopHistoriesAll() {
        List<History> distinctAll = dao.getDistinctTopHistoriesByQuizId(6L, Constants.FETCH_LIMIT);

        assertFalse(distinctAll.isEmpty());
        assertEquals(distinctAll.stream().map(History::getUserId).distinct().count(), distinctAll.size());
    }

    @Test
    public void testGetTopPerformersByRange() {
        List<History> lastDay = dao.getTopPerformersByQuizIdAndRange(6L, "last_day", Constants.FETCH_LIMIT);
        assertTrue(lastDay.stream().allMatch(h -> h.getCompletionDate().after(Timestamp.valueOf(LocalDateTime.now().minusDays(1)))));

        assertDoesNotThrow(() -> dao.getTopPerformersByQuizIdAndRange(6L, "last_week", Constants.FETCH_LIMIT));
    }


    @Test
    public void testUserHistoryByQuiz() {
        List<History> userQuiz = dao.getUserHistoryByQuiz(8L, 6L, Constants.FETCH_LIMIT);

        assertFalse(userQuiz.isEmpty());
        assertTrue(userQuiz.stream().allMatch(h -> h.getUserId() == 8L && h.getQuizId() == 6L));
    }


    @Test
    public void testUserFriendsHistoryByQuiz() {
        // Existing
        List<History> friendsQuizExisting = dao.getUserFriendsHistoryByQuiz(7L, 6L, Constants.FETCH_LIMIT);

        assertFalse(friendsQuizExisting.isEmpty());
        assertTrue(friendsQuizExisting.stream().allMatch(h -> h.getQuizId() == 6L && h.getUserId() != 7L));

        // Non-Existing
        List<History> friendsQuizNonExisting = dao.getUserFriendsHistoryByQuiz(7L, 4L, Constants.FETCH_LIMIT);
        assertTrue(friendsQuizNonExisting.isEmpty());
    }


    @Test
    public void testUserFriendsHistory() {
        List<History> friendsAll = dao.getUserFriendsHistory(7L, Constants.FETCH_LIMIT);

        assertEquals(2, friendsAll.size());
        assertTrue(friendsAll.stream().allMatch(h -> h.getUserId() != 7L));
    }


    @Test
    public void testStatisticsMethods() {
        long quizId = 6L;

        assertEquals(2, dao.getTotalAttempts(quizId));
        assertEquals(4.0, dao.getAverageScore(quizId), 0.001);
        assertEquals(5, dao.getMaximumScore(quizId));
        assertEquals(3, dao.getMinimumScore(quizId));
        assertEquals(8.5, dao.getAverageTime(quizId), 0.001);
    }


    @Test
    public void testInsertHistory_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(any(), anyInt())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert error"));

        HistoriesDao dao = new HistoriesDao(ds);
        History h = new History(1, 1, 1, 80, 10.5, null);

        assertThrows(RuntimeException.class, () -> dao.insertHistory(h));
    }


    // --- Mockito Tests ---


    @Test
    public void testGetHistoriesByUserId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getHistoriesByUserId(1L, 5));
    }


    @Test
    public void testGetHistoriesByQuizId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getHistoriesByQuizId(1L, 5));
    }


    @Test
    public void testGetTopNDistinctHistoriesByQuizId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTopNDistinctHistoriesByQuizId(1L, 5));
    }


    @Test
    public void testGetDistinctTopHistoriesByQuizId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getDistinctTopHistoriesByQuizId(1L, 5));
    }


    @Test
    public void testGetTopPerformersByQuizIdAndRange_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTopPerformersByQuizIdAndRange(1L, "last_week", 5));
    }


    @Test
    public void testGetUserHistoryByQuiz_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getUserHistoryByQuiz(1L, 1L, 5));
    }


    @Test
    public void testGetUserFriendsHistoryByQuiz_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getUserFriendsHistoryByQuiz(1L, 1L, 5));
    }


    @Test
    public void testGetUserFriendsHistory_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getUserFriendsHistory(1L, 5));
    }


    @Test
    public void testGetTotalAttempts_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Statistics query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTotalAttempts(1L));
    }


    @Test
    public void testGetAverageScore_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Statistics query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getAverageScore(1L));
    }


    @Test
    public void testGetMaximumScore_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Statistics query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getMaximumScore(1L));
    }


    @Test
    public void testGetMinimumScore_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Statistics query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getMinimumScore(1L));
    }


    @Test
    public void testGetAverageTime_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Statistics query error"));

        HistoriesDao dao = new HistoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getAverageTime(1L));
    }
}
