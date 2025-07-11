package daoTests;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.dao.*;
import org.ja.model.data.Achievement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;
import javax.sql.DataSource;
import java.sql.*;

/**
 * Unit tests for the AchievementsDao class using an in-memory H2 database.
 */
public class AchievementDaoTest extends BaseDaoTest{
    private AchievementsDao dao;


    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new AchievementsDao(basicDataSource);
    }


    @Test
    public void testGetExistingAchievementById() {
        Achievement a = dao.getAchievement(1L);
        assertNotNull(a);
        assertEquals("Brainstormer", a.getAchievementName());
        assertEquals("Finish 5 quizzes with above 80%", a.getAchievementDescription());
        assertNull(a.getAchievementPhoto());
    }


    @Test
    public void testGetNonExistentAchievement() {
        assertNull(dao.getAchievement(1225)); // ID not present
    }


    @Test
    public void testInsertAchievement() {
        Achievement newAchievement = new Achievement(0L, "Trivia Titan", "Answer 100 questions correctly", null);

        boolean inserted = dao.insertAchievement(newAchievement);
        assertTrue(inserted);

        long id = newAchievement.getAchievementId();
        assertTrue(id > 0, "ID should be auto-generated");

        Achievement fetched = dao.getAchievement(id);
        assertNotNull(fetched);
        assertEquals("Trivia Titan", fetched.getAchievementName());
        assertEquals("Answer 100 questions correctly", fetched.getAchievementDescription());
        assertNull(fetched.getAchievementPhoto());
    }


    @Test
    public void testInsertAchievementWithInvalidDataThrows() {
        Achievement invalid = new Achievement(0L, null, "desc", null);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAchievement(invalid));
        assertTrue(ex.getMessage().contains("Error inserting achievement"));
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertAchievementZeroRowsAffected() throws Exception {
        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockPreparedStatement);

        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        BasicDataSource mockBasicDataSource = mock(BasicDataSource.class);
        when(mockBasicDataSource.getConnection()).thenReturn(mockConnection);

        AchievementsDao mockedDao = new AchievementsDao(mockBasicDataSource);

        Achievement testAchievement = new Achievement(0L, "Test Achievement", "Test Description", null);

        boolean result = mockedDao.insertAchievement(testAchievement);

        assertFalse(result);
        assertEquals(0L, testAchievement.getAchievementId());
    }


    @Test
    public void testInsertAchievement_sqlExceptionThrown() throws Exception {
        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenThrow(new SQLException("DB failure"));

        BasicDataSource mockBasicDataSource = mock(BasicDataSource.class);
        when(mockBasicDataSource.getConnection()).thenReturn(mockConnection);

        AchievementsDao dao = new AchievementsDao(mockBasicDataSource);

        Achievement achievement = new Achievement(0L, "CrashTest", "SQL error test", null);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> dao.insertAchievement(achievement));

        assertTrue(thrown.getMessage().contains("Error inserting achievement"));
    }


    @Test
    public void testInsertAchievement_noGeneratedKey_throwsRuntimeException() throws Exception {
        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // no ID returned

        BasicDataSource mockBasicDataSource = mock(BasicDataSource.class);
        when(mockBasicDataSource.getConnection()).thenReturn(mockConnection);

        AchievementsDao dao = new AchievementsDao(mockBasicDataSource);

        Achievement achievement = new Achievement(0L, "MissingID", "Should throw", null);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> dao.insertAchievement(achievement));

        assertEquals("Insert succeeded but no ID was returned.", thrown.getMessage());
    }


    @Test
    public void testGetAchievement_sqlExceptionThrown() throws Exception {
        DataSource mockDataSource = mock(DataSource.class);
        Connection mockConnection = mock(Connection.class);

        when(mockDataSource.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenThrow(new SQLException("DB problem"));

        BasicDataSource mockBasicDataSource = mock(BasicDataSource.class);
        when(mockBasicDataSource.getConnection()).thenReturn(mockConnection);

        AchievementsDao dao = new AchievementsDao(mockBasicDataSource);

        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> dao.getAchievement(99));

        assertTrue(thrown.getMessage().contains("Error retrieving achievement"));
    }

}
