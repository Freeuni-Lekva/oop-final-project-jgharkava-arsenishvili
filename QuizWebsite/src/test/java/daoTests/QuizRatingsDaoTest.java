package daoTests;

import org.ja.dao.*;
import org.ja.model.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


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
