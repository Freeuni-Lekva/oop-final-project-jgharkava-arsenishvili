package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
create table challenges(
    challenge_id bigint primary key auto_increment,
    sender_user_id bigint not null,
    recipient_user_id bigint not null,
    quiz_id bigint not null,

    foreign key (sender_user_id) references users(user_id),
    foreign key (recipient_user_id) references users(user_id),
    foreign key (quiz_id) references quizzes(quiz_id)
);
 */
public class ChallengesDao {
    private final BasicDataSource dataSource;

    public ChallengesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertChallenge(Challenge challenge) {
        String sql = "INSERT INTO challenges (sender_user_id, recipient_user_id, quiz_id) VALUES (?,?,?)";
        try(Connection c= dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, challenge.getSenderUserId());
            ps.setLong(2, challenge.getRecipientUserId());
            ps.setLong(3, challenge.getQuizId());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next())
                    challenge.setChallengeId(rs.getLong("challenge_id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting challenge into database", e);
        }
    }

    public void removeChallenge(long challengeId) {
        String sql = "DELETE FROM challenges WHERE challenge_id=" + challengeId;

        try (Connection c= dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing challenge from database", e);
        }
    }

    public ArrayList<Challenge> challengesAsSender(long senderId){
        ArrayList<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM challenges WHERE sender_user_id=" + senderId;

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();
            while (rs.next())
                challenges.add(retrieveChallenge(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error querying challenges of a sender user", e);
        }

        return challenges;
    }

    public ArrayList<Challenge> challengesAsReceiver(long receiverId){
        ArrayList<Challenge> challenges = new ArrayList<>();

        String sql = "SELECT * FROM challenges WHERE recipient_user_id=" + receiverId;

        try (Connection c= dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();

            while(rs.next())
                challenges.add(retrieveChallenge(rs));
            
        } catch (SQLException e) {
            throw new RuntimeException("Error querying challenges of a recipient user", e);
        }

        return challenges;
    }



    private Challenge retrieveChallenge(ResultSet rs) throws SQLException {
        return new Challenge(rs.getLong("challenge_id"), rs.getLong("sender_user_id"),
                rs.getLong("recipient_user_id"), rs.getLong("quiz_id"));
    }
}
