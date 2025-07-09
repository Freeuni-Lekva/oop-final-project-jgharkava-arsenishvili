package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for administrator-specific operations.
 */
public class AdministratorsDao {
    private final BasicDataSource ds;


    /**
     * Constructs an AdministratorsDao with given data source
     *
     * @param ds the database connection pool
     */
    public AdministratorsDao(BasicDataSource ds) {
        this.ds = ds;
    }


    /**
     * Promotes a user to administrator by updating their status.
     *
     * @param userId the ID of the user to promote
     * @return true if the user was updated; false if not found
     */
    public boolean promoteToAdministrator(long userId) {
        String sql = "UPDATE users SET user_status = 'administrator' WHERE user_id = ? AND user_status = 'user'";

        try (Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql)) {

            st.setLong(1, userId);

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error promoting user to administrator", e);
        }
    }

    /**
     * Returns the number of unique quizzes that have been taken (appear in history).
     *
     * @return the count of taken quizzes
     */
    public int getTakenQuizzesCount(){
        String sql = "SELECT COUNT(DISTINCT quiz_id) FROM history";

        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("Error getting count of taken quizzes", e);
        }
        return 0;
    }


    /**
     * Removes a user by their ID.
     *
     * @param id the user_id to remove
     * @return true if the user was removed; false if not found
     */
    public boolean removeUserById(long id) {
        try (Connection c = ds.getConnection()){
            try (PreparedStatement st = c.prepareStatement("DELETE FROM users WHERE user_id = ?")) {

                st.setLong(1, id);

                return st.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user from database", e);
        }
    }

    /**
     * Retrieves the total number of users in the system.
     *
     * @return total user count
     */
    public int getUserCount(){
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql);
            ResultSet rs = st.executeQuery()){

            if (rs.next())
                return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException("Error getting user count", e);
        }
        return 0;
    }


    /**
     * Clears all history records for a specific quiz.
     *
     * @param quizId the quiz_id whose history should be deleted
     * @return true if any rows were deleted; false otherwise
     */
    public boolean clearQuizHistory(long quizId) {
        String sql = "DELETE FROM history WHERE quiz_id = ?";

        try (Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql)) {

            st.setLong(1, quizId);

            return st.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error clearing quiz history", e);
        }
    }
}
