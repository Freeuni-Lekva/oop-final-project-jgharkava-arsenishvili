package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Achievement;

import java.util.ArrayList;
/*
create table user_achievement(
    user_id bigint not null,
    achievement_id bigint not null,
    achievement_date timestamp default current_timestamp,

    primary key (user_id, achievement_id),
    foreign key (user_id) references users(user_id) on delete cascade,
    foreign key (achievement_id) references achievements(achievement_id) on delete cascade
);
 */
import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.UserAchievement;
import org.ja.model.user.User;

import java.sql.*;
import java.util.ArrayList;

public class UserAchievementsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public UserAchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Grants the given achievement to a user
    public void insertAchievement(UserAchievement ua) {
        if(contains(ua)){
            return;
        }
        String sql = "INSERT INTO user_achievement (user_id, achievement_id) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, ua.getUserId());
            ps.setLong(2, ua.getAchievementId());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                cnt++;
                ua.setAchievementDate(rs.getTimestamp(3));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user achievement", e);
        }
    }

    // Removes the specified achievement from a user
    public void removeAchievement(UserAchievement ua) {
        if(!contains(ua)){
            return;
        }
        String sql = "DELETE FROM user_achievement WHERE user_id = ? AND achievement_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ua.getUserId());
            ps.setLong(2, ua.getAchievementId());

            ps.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove user achievement", e);
        }
    }

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
        return  cnt;
    }
    private UserAchievement retrieveUserAchievement(ResultSet rs) throws SQLException {
        return new UserAchievement(rs.getLong("user_id"), rs.getLong("achievement_id"),
                rs.getTimestamp("achievement_date"));
    }
}
