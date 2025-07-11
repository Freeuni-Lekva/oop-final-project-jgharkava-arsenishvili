package daoTests;

import org.ja.dao.*;
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
import static org.mockito.Mockito.*;


/**
 * Unit tests for the QuizRatingsDao class using an in-memory H2 database.
 */
public class QuizRatingsDaoTest extends BaseDaoTest{
    private QuizRatingsDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new QuizRatingsDao(basicDataSource);
    }

    @Test
    public void testInsertNewRatingAndAverageUpdate() {
        QuizRating rating = new QuizRating(4L, 8L, 5, "Great quiz!");
        assertTrue(dao.insertQuizRating(rating));

        QuizRating stored = dao.getQuizRatingByUserIdQuizId(8L, 4L);
        assertEquals(4L, stored.getQuizId());
        assertEquals(8L, stored.getUserId());
        assertEquals(5, stored.getRating());
        assertEquals("Great quiz!", stored.getReview());

        // Ensure average_rating in quizzes table is updated
        double avg = getQuizAverageRating(4L);
        assertEquals(4.5, avg);
    }


    @Test
    public void testUpdateExistingRating() {
        QuizRating rating = new QuizRating(5L, 8L, 2, "Not bad");
        assertTrue(dao.insertQuizRating(rating));

        QuizRating updated = new QuizRating(5L, 8L, 5, "Much better now");
        assertTrue(dao.insertQuizRating(updated));  // should trigger update

        QuizRating stored = dao.getQuizRatingByUserIdQuizId(8L, 5L);
        assertEquals(5, stored.getRating());
        assertEquals("Much better now", stored.getReview());
    }


    @Test
    public void testGetQuizRatingsByQuizIdLimit() {
        dao.insertQuizRating(new QuizRating(4L, 5L, 3, "Nice"));
        dao.insertQuizRating(new QuizRating(4L, 6L, 4, "Cool"));
        dao.insertQuizRating(new QuizRating(4L, 7L, 5, "Perfect"));

        List<QuizRating> limited = dao.getQuizRatingsByQuizId(4L, 2);
        assertEquals(2, limited.size());

        assertTrue(limited.stream().allMatch(r -> r.getQuizId() == 4L));
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertQuizRating_throwsExceptionOnInsert() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        QuizRatingsDao dao = new QuizRatingsDao(ds);
        QuizRating qr = new QuizRating(1L, 1L, 5, "Review");

        assertThrows(RuntimeException.class, () -> dao.insertQuizRating(qr));
    }


    @Test
    public void testInsertQuizRating_throwsExceptionOnUpdateAfterDuplicate() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);

        Connection insertConn = mock(Connection.class);
        PreparedStatement insertPs = mock(PreparedStatement.class);

        Connection updateConn = mock(Connection.class);
        PreparedStatement updatePs = mock(PreparedStatement.class);

        when(ds.getConnection())
                .thenReturn(insertConn)  // Insert connection
                .thenReturn(updateConn); // Update connection

        when(insertConn.prepareStatement(anyString())).thenReturn(insertPs);
        when(insertPs.executeUpdate()).thenThrow(new SQLException("Duplicate", "23000", 1062));

        when(updateConn.prepareStatement(anyString())).thenReturn(updatePs);
        when(updatePs.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizRatingsDao dao = new QuizRatingsDao(ds);
        QuizRating qr = new QuizRating(1L, 1L, 5, "Review");

        assertThrows(RuntimeException.class, () -> dao.insertQuizRating(qr));
    }


    @Test
    public void testGetQuizRatingByUserIdQuizId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizRatingsDao dao = new QuizRatingsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizRatingByUserIdQuizId(1L, 1L));
    }


    @Test
    public void testGetQuizRatingsByQuizId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizRatingsDao dao = new QuizRatingsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizRatingsByQuizId(1L, 5));
    }


    @Test
    public void testUpdateQuizRating_throwsExceptionOnSelect() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection selectConn = mock(Connection.class);
        PreparedStatement selectPs = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(selectConn);
        when(selectConn.prepareStatement(startsWith("SELECT AVG"))).thenReturn(selectPs);
        when(selectPs.executeQuery()).thenThrow(new SQLException("Select AVG failed"));

        QuizRatingsDao dao = new QuizRatingsDao(ds);

        // Trigger updateQuizRating indirectly by inserting
        Connection insertConn = mock(Connection.class);
        PreparedStatement insertPs = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(insertConn).thenReturn(selectConn);

        when(insertConn.prepareStatement(startsWith("INSERT"))).thenReturn(insertPs);
        when(insertPs.executeUpdate()).thenReturn(1);

        QuizRating qr = new QuizRating(1L, 1L, 5, "Review");

        assertThrows(RuntimeException.class, () -> dao.insertQuizRating(qr));
    }


    @Test
    public void testUpdateQuizRating_throwsExceptionOnUpdate() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        PreparedStatement selectPs = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        PreparedStatement updatePs = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);

        when(conn.prepareStatement(startsWith("SELECT AVG"))).thenReturn(selectPs);
        when(selectPs.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getDouble("avgRating")).thenReturn(4.5);

        when(conn.prepareStatement(startsWith("UPDATE quizzes SET"))).thenReturn(updatePs);
        when(updatePs.executeUpdate()).thenThrow(new SQLException("Update failed"));

        // Setup insert to succeed so updateQuizRating is called
        PreparedStatement insertPs = mock(PreparedStatement.class);
        when(conn.prepareStatement(startsWith("INSERT"))).thenReturn(insertPs);
        when(insertPs.executeUpdate()).thenReturn(1);

        QuizRatingsDao dao = new QuizRatingsDao(ds);
        QuizRating qr = new QuizRating(1L, 1L, 5, "Review");

        assertThrows(RuntimeException.class, () -> dao.insertQuizRating(qr));
    }



    // --- Helper Methods ---


    /**
     * Retrieves the average rating of a quiz from the {@code quizzes} table.
     *
     * @param quizId the ID of the quiz whose average rating is to be retrieved
     * @return the average rating as a {@code double}
     * @throws RuntimeException if the quiz ID is not found or a database error occurs
     */
    private double getQuizAverageRating(long quizId) {
        String sql = "SELECT average_rating FROM quizzes WHERE quiz_id = ?";

        try (Connection connection = basicDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("average_rating");
                } else {
                    throw new RuntimeException("No quiz rating by this quizId");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch quiz average rating", e);
        }
    }
}
