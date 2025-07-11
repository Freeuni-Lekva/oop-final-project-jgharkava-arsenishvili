package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.UserAchievement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


/**
 * DAO class for accessing and managing user achievements stored in the 'user_achievement' table.
 */
public class UserAchievementsDao {
    private final BasicDataSource dataSource;


    /**
     * Constructs a new UserAchievementsDao with the specified data source.
     *
     * @param dataSource the connection pool for obtaining database connections
     */
    public UserAchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Retrieves all achievements earned by a specific user.
     *
     * @param userId the ID of the user whose achievements are being retrieved
     * @param limit limit the maximum number of user's achievements to return
     * @return a list of {@link UserAchievement} objects associated with the user
     * @throws RuntimeException if a SQL error occurs
     */
    public List<UserAchievement> getUserAchievements(long userId, int limit) {
        List<UserAchievement> achievements = new ArrayList<>();

        String sql = "SELECT * FROM user_achievement WHERE user_id = ? LIMIT ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    achievements.add(retrieveUserAchievement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve user achievements", e);
        }

        return achievements;
    }


    // --- Helper Methods ---

    /**
     * Maps a {@link ResultSet} row to a {@link UserAchievement} object.
     *
     * @param rs the result set containing the current row
     * @return the mapped {@link UserAchievement} object
     * @throws SQLException if an error occurs while accessing the result set
     */
    private UserAchievement retrieveUserAchievement(ResultSet rs) throws SQLException {
        return new UserAchievement(rs.getLong("user_id"), rs.getLong("achievement_id"),
                rs.getTimestamp("achievement_date"));
    }
}
