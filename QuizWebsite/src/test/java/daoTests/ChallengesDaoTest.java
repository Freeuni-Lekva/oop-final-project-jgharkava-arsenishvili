package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.*;
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
 * Unit tests for the ChallengesDao class using an in-memory H2 database.
 */
public class ChallengesDaoTest extends BaseDaoTest{
    private ChallengesDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new ChallengesDao(basicDataSource);
    }


    @Test
    public void testInsertChallenge() {
        Challenge challenge = new Challenge(0, 7, 6, 4); // Ani (7) -> Gio (6) for quiz_id 4

        boolean inserted = dao.insertChallenge(challenge);
        assertTrue(inserted);
        assertTrue(challenge.getChallengeId() > 0);
    }


    @Test
    public void testRemoveChallenge() {
        List<Challenge> challenges = dao.challengesAsReceiver(5, Constants.FETCH_LIMIT);
        assertFalse(challenges.isEmpty());

        Challenge challenge = challenges.get(0);
        boolean removed = dao.removeChallenge(challenge.getChallengeId());
        assertTrue(removed);

        List<Challenge> updated = dao.challengesAsReceiver(5, Constants.FETCH_LIMIT);
        assertFalse(updated.contains(challenge));
    }


    @Test
    public void testChallengesAsReceiver() {
        List<Challenge> receiverChallenges = dao.challengesAsReceiver(5, Constants.FETCH_LIMIT); // Mariam <- quiz_id 5 from Ani
        assertEquals(1, receiverChallenges.size());
        Challenge challenge = receiverChallenges.get(0);
        assertEquals(5, challenge.getQuizId());
        assertEquals(7, challenge.getSenderUserId());
    }


    @Test
    public void testChallengesForUserWithNoEntries() {
        List<Challenge> receiver = dao.challengesAsReceiver(1225, Constants.FETCH_LIMIT);
        assertTrue(receiver.isEmpty());
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertChallenge_throwsException() throws Exception {
        BasicDataSource mockDataSource = mock(BasicDataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(any(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        ChallengesDao dao = new ChallengesDao(mockDataSource);
        Challenge challenge = new Challenge(1, 2, 3); // dummy data

        assertThrows(RuntimeException.class, () -> dao.insertChallenge(challenge));
    }


    @Test
    public void testInsertChallenge_missingGeneratedKey_throwsException() throws Exception {
        BasicDataSource mockDataSource = mock(BasicDataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(any(), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // no key returned

        ChallengesDao dao = new ChallengesDao(mockDataSource);
        Challenge challenge = new Challenge(1, 2, 3);

        assertThrows(RuntimeException.class, () -> dao.insertChallenge(challenge));
    }


    @Test
    public void testRemoveChallenge_throwsException() throws Exception {
        BasicDataSource mockDataSource = mock(BasicDataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenThrow(new SQLException("Delete failed"));

        ChallengesDao dao = new ChallengesDao(mockDataSource);

        assertThrows(RuntimeException.class, () -> dao.removeChallenge(99L));
    }


    @Test
    public void testChallengesAsReceiver_throwsException() throws Exception {
        BasicDataSource mockDataSource = mock(BasicDataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(any())).thenReturn(mockStatement);
        when(mockStatement.executeQuery()).thenThrow(new SQLException("Query failed"));

        ChallengesDao dao = new ChallengesDao(mockDataSource);

        assertThrows(RuntimeException.class, () -> dao.challengesAsReceiver(1L, 5));
    }
}
