package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChallengesDao {
    private final BasicDataSource dataSource;
    private long cnt = 0;

    public ChallengesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertChallenge(Challenge challenge) {
        String sql = "INSERT INTO challenges (sender_user_id, recipient_user_id, quiz_id) VALUES (?,?,?)";

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, challenge.getSenderUserId());
            ps.setLong(2, challenge.getRecipientUserId());
            ps.setLong(3, challenge.getQuizId());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) {
                    cnt++;
                    challenge.setChallengeId(rs.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting challenge into database", e);
        }
    }

    public void removeChallenge(long challengeId) {
        String sql = "DELETE FROM challenges WHERE challenge_id = ?";

        try (Connection c= dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, challengeId);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing challenge from database", e);
        }
    }

    /// returns empty list if no challenges found
    public ArrayList<Challenge> challengesAsSender(long senderId){
        ArrayList<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM challenges WHERE sender_user_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, senderId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    challenges.add(retrieveChallenge(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying challenges of a sender user", e);
        }

        return challenges;
    }

    /// returns empty list if no challenges found
    public ArrayList<Challenge> challengesAsReceiver(long receiverId){
        ArrayList<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM challenges WHERE recipient_user_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, receiverId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    challenges.add(retrieveChallenge(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying challenges of a recipient user", e);
        }

        return challenges;
    }

    public boolean contains(Challenge c){
        if(c == null){
            return false;
        }

        String sql = "SELECT COUNT(*) FROM challenges WHERE  challenge_id=? AND sender_user_id=?" +
                "AND recipient_user_id=? AND quiz_id=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, c.getChallengeId());
            ps.setLong(2, c.getSenderUserId());
            ps.setLong(3, c.getRecipientUserId());
            ps.setLong(4, c.getQuizId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    public boolean contains(long cid){
        String sql = "SELECT COUNT(*) FROM challenges WHERE challenge_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, cid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    public long getCount(){
        return cnt;
    }

    private Challenge retrieveChallenge(ResultSet rs) throws SQLException {
        return new Challenge(rs.getLong("challenge_id"), rs.getLong("sender_user_id"),
                rs.getLong("recipient_user_id"), rs.getLong("quiz_id"));
    }
}
