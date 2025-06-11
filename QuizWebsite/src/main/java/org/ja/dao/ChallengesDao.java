package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Challenge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private BasicDataSource dataSource;
    public ChallengesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertChallenge(Challenge challenge) {
        String sql="INSERT INTO challenges (sender_user_id, recipient_user_id, quiz_id) VALUES (?,?,?)";
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, challenge.getSenderUserId());
            ps.setLong(2, challenge.getRecipientUserId());
            ps.setLong(3, challenge.getQuizId());
            ps.execute();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                challenge.setChallengeId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeChallenge(long challengeId) {
        String sql="DELETE FROM challenges WHERE challenge_id="+challengeId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ps.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Challenge> challengesAsSender(long senderId){
        String sql="SELECT * FROM challenges WHERE sender_user_id="+senderId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<Challenge> challenges = new ArrayList<>();
            while(rs.next()){
                Challenge challenge = new Challenge();
                challenge.setChallengeId(rs.getLong(1));
                challenge.setSenderUserId(rs.getLong("sender_user_id"));
                challenge.setRecipientUserId(rs.getLong("recipient_user_id"));
                challenge.setQuizId(rs.getLong("quiz_id"));
                challenges.add(challenge);
            }
            return challenges;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Challenge> challengesAsReceiver(long receiverId){
        String sql="SELECT * FROM challenges WHERE recipient_user_id="+receiverId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<Challenge> challenges = new ArrayList<>();
            while(rs.next()){
                Challenge challenge = new Challenge();
                challenge.setChallengeId(rs.getLong(1));
                challenge.setSenderUserId(rs.getLong("sender_user_id"));
                challenge.setRecipientUserId(rs.getLong("recipient_user_id"));
                challenge.setQuizId(rs.getLong("quiz_id"));
                challenges.add(challenge);
            }
            return challenges;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
