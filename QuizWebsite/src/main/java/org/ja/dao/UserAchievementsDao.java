package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.UserAchievement;
import java.sql.*;
import java.util.ArrayList;

public class UserAchievementsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public UserAchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    ///  if UserAchievement with same user and achievement ids exists, returns RuntimeException
    public void insertAchievement(UserAchievement ua) {
        String sql = "INSERT INTO user_achievement (user_id, achievement_id) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ua.getUserId());
            ps.setLong(2, ua.getAchievementId());

            ps.executeUpdate();

            cnt++;

            String s = "SELECT achievement_date FROM user_achievement WHERE " +
                    "user_id = ? AND achievement_id = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(s)){
                preparedStatement.setLong(1, ua.getUserId());
                preparedStatement.setLong(2, ua.getAchievementId());

                try (ResultSet r = preparedStatement.executeQuery()) {
                    if (r.next()) {
                        ua.setAchievementDate(r.getTimestamp("achievement_date"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user achievement", e);
        }
    }

    // Removes the specified achievement from a user
    public void removeAchievement(UserAchievement ua) {
        String sql = "DELETE FROM user_achievement WHERE user_id = ? AND achievement_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ua.getUserId());
            ps.setLong(2, ua.getAchievementId());

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove user achievement", e);
        }
    }

    // TODO change to ArrayList<Long>
    // Returns all user_achievement rows for a user
    public ArrayList<UserAchievement> getUserAchievements(long userId) {
        ArrayList<UserAchievement> achievements = new ArrayList<>();

        String sql = "SELECT * FROM user_achievement WHERE user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    achievements.add(retrieveUserAchievement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user achievements", e);
        }

        return achievements;
    }

    public boolean contains(UserAchievement ua) {
        if(ua==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM user_achievement WHERE user_id = ? AND achievement_id=?" +
                "AND achievement_date=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, ua.getUserId());
            ps.setLong(2, ua.getAchievementId());
            ps.setTimestamp(3, ua.getAchievementDate());
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    public long getCount(){
        return  cnt;
    }

    private UserAchievement retrieveUserAchievement(ResultSet rs) throws SQLException {
        return new UserAchievement(rs.getLong("user_id"), rs.getLong("achievement_id"),
                rs.getTimestamp("achievement_date"));
    }
}
