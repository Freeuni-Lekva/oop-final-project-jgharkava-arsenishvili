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

import java.sql.*;
import java.util.ArrayList;

public class UserAchievementsDao {
    private final BasicDataSource dataSource;

    public UserAchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Grants the given achievement to a user
    public void insertAchievement(UserAchievement ua) {
        String sql = "INSERT INTO user_achievement (user_id, achievement_id, achievement_date) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, ua.getUserId());
            ps.setLong(2, ua.getAchievementId());
            ps.setTimestamp(3, ua.getAchievementDate());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert user achievement", e);
        }
    }

    // Removes the specified achievement from a user
    public void removeAchievement(long userId, long achievementId) {
        String sql = "DELETE FROM user_achievement WHERE user_id = "+userId+" AND achievement_id = "+achievementId;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();

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
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                UserAchievement ua = new UserAchievement();
                ua.setUserId(rs.getLong("user_id"));
                ua.setAchievementId(rs.getLong("achievement_id"));
                ua.setAchievementDate(rs.getTimestamp("achievement_date"));
                achievements.add(ua);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user achievements", e);
        }

        return achievements;
    }
}
