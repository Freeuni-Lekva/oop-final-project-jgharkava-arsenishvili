package daoTests;

import org.ja.dao.*;
import org.ja.model.data.Match;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


/**
 * Unit tests for the MatchesDao class using an in-memory H2 database.
 */
public class MatchesDaoTest extends BaseDaoTest{
    private MatchesDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new MatchesDao(basicDataSource);
    }


    @Test
    public void testInsertMatch() {
        Match newMatch = new Match(-1, 7L, "New Inventor", "New Invention");

        assertTrue(dao.insertMatch(newMatch));
        assertTrue(newMatch.getMatchId() > 0);

        List<Match> matches = dao.getQuestionMatches(7L);
        assertTrue(matches.stream().anyMatch(m ->
                m.getLeftMatch().equals("New Inventor") &&
                        m.getRightMatch().equals("New Invention")));
    }


    @Test
    public void testRemoveMatch() {
        List<Match> matches = dao.getQuestionMatches(7L);
        assertFalse(matches.isEmpty());

        long matchId = matches.get(0).getMatchId();

        assertTrue(dao.removeMatch(matchId));

        List<Match> updated = dao.getQuestionMatches(7L);
        assertFalse(updated.stream().anyMatch(m -> m.getMatchId() == matchId));
    }


    @Test
    public void testGetQuestionMatches() {
        List<Match> matches = dao.getQuestionMatches(7L);

        assertNotNull(matches);
        assertEquals(4, matches.size());

        for (Match m : matches) {
            assertEquals(7L, m.getQuestionId());
            assertNotNull(m.getLeftMatch());
            assertNotNull(m.getRightMatch());
        }
    }


    @Test
    public void testUpdateLeftMatch() {
        List<Match> matches = dao.getQuestionMatches(7L);
        long matchId = matches.get(0).getMatchId();

        boolean updated = dao.updateLeftMatch(matchId, "Updated Left");
        assertTrue(updated);

        Match updatedMatch = dao.getQuestionMatches(7L).stream()
                .filter(m -> m.getMatchId() == matchId)
                .findFirst()
                .orElseThrow();

        assertEquals("Updated Left", updatedMatch.getLeftMatch());
    }


    @Test
    public void testUpdateRightMatch() {
        List<Match> matches = dao.getQuestionMatches(7L);
        long matchId = matches.get(0).getMatchId();

        boolean updated = dao.updateRightMatch(matchId, "Updated Right");
        assertTrue(updated);

        Match updatedMatch = dao.getQuestionMatches(7L).stream()
                .filter(m -> m.getMatchId() == matchId)
                .findFirst()
                .orElseThrow();

        assertEquals("Updated Right", updatedMatch.getRightMatch());
    }


    @Test
    public void testInsertMatchUpdatesCounts() {
        long questionId = 7L;
        long quizId = 6L;

        int oldNumAnswers = getQuestionNumAnswers(questionId);
        int oldQuizScore = getQuizScore(quizId);

        Match newMatch = new Match(-1, questionId, "Tesla", "AC Motor");

        assertTrue(dao.insertMatch(newMatch));
        assertTrue(newMatch.getMatchId() > 0);

        assertEquals(oldNumAnswers + 1, getQuestionNumAnswers(questionId));
        assertEquals(oldQuizScore + 1, getQuizScore(quizId));
    }


    @Test
    public void testRemoveMatchUpdatesCounts() {
        long questionId = 7L;
        long quizId = 6L;

        Match match = dao.getQuestionMatches(questionId).get(0);
        long matchId = match.getMatchId();

        int oldNumAnswers = getQuestionNumAnswers(questionId);
        int oldQuizScore = getQuizScore(quizId);

        assertTrue(dao.removeMatch(matchId));

        assertEquals(oldNumAnswers - 1, getQuestionNumAnswers(questionId));
        assertEquals(oldQuizScore - 1, getQuizScore(quizId));
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertMatch_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), anyInt())).thenThrow(new SQLException("Insert error"));

        MatchesDao dao = new MatchesDao(ds);
        Match m = new Match(0, 1L, "left", "right");

        assertThrows(RuntimeException.class, () -> dao.insertMatch(m));
    }


    @Test
    public void testRemoveMatch_throwsExceptionOnDelete() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Delete error"));

        MatchesDao dao = new MatchesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.removeMatch(1L));
    }


    @Test
    public void testGetQuestionMatches_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Query error"));

        MatchesDao dao = new MatchesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuestionMatches(1L));
    }


    @Test
    public void testUpdateLeftMatch_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Update error"));

        MatchesDao dao = new MatchesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateLeftMatch(1L, "newLeft"));
    }


    @Test
    public void testUpdateRightMatch_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Update error"));

        MatchesDao dao = new MatchesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateRightMatch(1L, "newRight"));
    }
}
