package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing quiz challenges between users.
 * A challenge is a request from one user to another to take a specific quiz.
 */
public class ChallengesDao {
    private final BasicDataSource dataSource;


    /**
     * Constructs a new ChallengesDao with the specified data source.
     *
     * @param dataSource the database connection pool to use for queries
     */
    public ChallengesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new challenge record into the database and sets the generated challenge ID.
     *
     * @param challenge the Challenge object to insert; must contain valid sender, recipient, and quiz IDs
     * @return true if the challenge was inserted successfully; false otherwise
     * @throws RuntimeException if the insert succeeds but no generated ID is returned, or a database error occurs
     */
    public boolean insertChallenge(Challenge challenge) {
        String sql = "INSERT INTO challenges (sender_user_id, recipient_user_id, quiz_id) VALUES (?,?,?)";

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, challenge.getSenderUserId());
            ps.setLong(2, challenge.getRecipientUserId());
            ps.setLong(3, challenge.getQuizId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) {
                    challenge.setChallengeId(rs.getLong(1));
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting challenge into database", e);
        }
    }


    /**
     * Removes a challenge from the database based on its ID.
     *
     * @param challengeId the ID of the challenge to remove
     * @return true if the challenge was removed; false if it did not exist
     * @throws RuntimeException if a database error occurs
     */
    public boolean removeChallenge(long challengeId) {
        String sql = "DELETE FROM challenges WHERE challenge_id = ?";

        try (Connection c= dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, challengeId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing challenge from database", e);
        }
    }


    /**
     * Retrieves all challenges where the given user is the recipient.
     *
     * @param receiverId the ID of the user who received the challenges
     * @return a list of Challenge objects received by the specified user (empty if none)
     * @throws RuntimeException if a database error occurs
     */
    public List<Challenge> challengesAsReceiver(long receiverId, int limit){
        ArrayList<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM challenges WHERE recipient_user_id = ? LIMIT ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, receiverId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    challenges.add(retrieveChallenge(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying challenges of a recipient user", e);
        }

        return challenges;
    }


    // --- Helper Methods ---


    /**
     * Maps the current row of a ResultSet to a Challenge object.
     *
     * @param rs the ResultSet positioned at a row from the challenges table
     * @return a populated Challenge object
     * @throws SQLException if a database access error occurs
     */
    private Challenge retrieveChallenge(ResultSet rs) throws SQLException {
        return new Challenge(rs.getLong("challenge_id"), rs.getLong("sender_user_id"),
                rs.getLong("recipient_user_id"), rs.getLong("quiz_id"));
    }
}

