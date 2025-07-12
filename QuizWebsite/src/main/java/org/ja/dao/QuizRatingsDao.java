package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.QuizRating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing quiz ratings.
 * Handles inserting, updating, and retrieving quiz ratings,
 * and updates the average rating of a quiz after any modification.
 */
public class QuizRatingsDao {
    private final BasicDataSource dataSource;


    /**
     * Constructs a QuizRatingsDao with the given data source.
     *
     * @param dataSource the database connection pool to use
     */
    public QuizRatingsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new quiz rating or updates the existing one
     * if a rating by the same user for the same quiz already exists.
     * After insert or update, it also updates the average rating of the quiz.
     *
     * @param qr the QuizRating object containing rating details
     * @return true if insert or update succeeded, false otherwise
     * @throws RuntimeException if any SQL operation fails
     */
    public boolean insertQuizRating(QuizRating qr) {
        String sql = "INSERT INTO quiz_rating (quiz_id, user_id, rating, review) " +
                "VALUES (?, ?, ?, ?) ";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, qr.getQuizId());
            ps.setLong(2, qr.getUserId());
            ps.setInt(3, qr.getRating());
            ps.setString(4, qr.getReview());

            if (ps.executeUpdate() == 0) {
                return false;
            }

        } catch (SQLException e) {

            // if already exists (quiz_id, user_id) pair
            if (e.getErrorCode() == 1062 || e.getErrorCode() == 23505) {
                String updateSql = "UPDATE quiz_rating SET rating = ?, review = ? WHERE quiz_id = ? AND user_id = ?";

                try (Connection c2 = dataSource.getConnection();
                     PreparedStatement ps2 = c2.prepareStatement(updateSql)) {

                    ps2.setInt(1, qr.getRating());
                    ps2.setString(2, qr.getReview());
                    ps2.setLong(3, qr.getQuizId());
                    ps2.setLong(4, qr.getUserId());

                    if (ps2.executeUpdate() == 0){
                        return false;
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException("Failed to update quiz_rating after duplicate insert", ex);
                }
            } else {
                throw new RuntimeException("Error inserting or updating quiz rating in database", e);
            }
        }

        updateQuizRating(qr.getQuizId());
        return true;
    }


    /**
     * Retrieves a quiz rating given a user ID.
     * If multiple ratings exist for different quizzes, only one (arbitrary) is returned.
     *
     * @param userId the ID of the user whose rating is to be fetched
     * @return a QuizRating object if found; otherwise, null
     * @throws RuntimeException if the query fails
     */
    public QuizRating getQuizRatingByUserIdQuizId(long userId, long quizId){
        String sql = "SELECT * FROM quiz_rating WHERE user_id = ? and quiz_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, quizId);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return retrieveQuizRating(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz rating by user id from database", e);
        }
    }


    /**
     * Retrieves a limited number of quiz ratings for a specific quiz.
     *
     * @param quizId the quiz ID for which ratings are retrieved
     * @param limit the maximum number of ratings to return
     * @return a list of QuizRating objects for the given quiz
     * @throws RuntimeException if the query fails
     */
    public List<QuizRating> getQuizRatingsByQuizId(long quizId, int limit){
        List<QuizRating> quizRatings = new ArrayList<>();

        String sql = "SELECT DISTINCT * FROM quiz_rating WHERE quiz_id = ? LIMIT ?";

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    quizRatings.add(retrieveQuizRating(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz rating by quiz id from database", e);
        }

        return quizRatings;
    }


    // --- Helper Methods ---


    /**
     * Parses a ResultSet row into a QuizRating object.
     *
     * @param rs the ResultSet positioned at a valid row
     * @return a QuizRating object populated with values from the row
     * @throws SQLException if column access fails
     */
    private QuizRating retrieveQuizRating(ResultSet rs) throws SQLException {
        return new QuizRating(rs.getLong("quiz_id"), rs.getLong("user_id"),
                rs.getInt("rating"), rs.getString("review"));
    }


    /**
     * Recalculates and updates the average rating of a quiz
     * based on all user-submitted ratings for that quiz.
     *
     * @param quizId the quiz ID whose average rating should be updated
     * @throws RuntimeException if any SQL operation fails
     */
    private void updateQuizRating(long quizId) {
        String selectSQL = "SELECT AVG(rating) AS avgRating FROM quiz_rating WHERE quiz_id = ?";
        String updateSQL = "UPDATE quizzes SET average_rating = ? WHERE quiz_id = ?";

        double averageRating = -1;

        // 1. Fetch average rating
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(selectSQL)) {

            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    averageRating = rs.getDouble("avgRating");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying average of quiz ratings from quiz_rating table", e);
        }

        if (averageRating == -1) {
            return;
        }

        // 2. Update average rating in quizzes table
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(updateSQL)) {

            ps.setDouble(1, averageRating);
            ps.setLong(2, quizId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating average rating of quiz in quizzes table", e);
        }
    }
}
