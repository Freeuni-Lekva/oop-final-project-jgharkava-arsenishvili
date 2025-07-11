package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.Achievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for managing achievements in the database.
 */
public class AchievementsDao {
    private final BasicDataSource dataSource;

    /**
     * Constructs an AchievementsDao with the given data source.
     *
     * @param dataSource the database connection pool
     */
    public AchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Inserts a new achievement into the database.
     *
     * @param achievement the Achievement object to insert
     * @return true if the achievement was inserted successfully and ID generated; false otherwise
     * @throws RuntimeException if id was not returned or other kind of sql error
     */
    public boolean insertAchievement(Achievement achievement){
        String sql = "INSERT INTO achievements (achievement_name, " +
                "achievement_description, achievement_photo) VALUES (?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, achievement.getAchievementName());
            ps.setString(2, achievement.getAchievementDescription());
            ps.setString(3, achievement.getAchievementPhoto());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) return false; // No row inserted

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()){
                    achievement.setAchievementId(rs.getLong(1));
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }

            }

            return true;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error inserting achievement into database", e);
        }
    }

    /**
     * Retrieves an achievement by its ID.
     *
     * @param id the ID of the achievement to retrieve
     * @return the Achievement object if found; null otherwise
     */
    public Achievement getAchievement(long id) {
        String sql = "SELECT * FROM achievements WHERE achievement_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return retrieveAchievement(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving achievement", e);
        }

        return null;
    }


    // --- Helper Methods ---


    /**
     * Helper method to create an Achievement object from the current row of a ResultSet.
     *
     * @param rs the ResultSet positioned at the row to extract data from
     * @return an Achievement object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or the column labels are invalid
     */
    private Achievement retrieveAchievement(ResultSet rs) throws SQLException {
        return new Achievement(rs.getLong("achievement_id"), rs.getString("achievement_name"),
                rs.getString("achievement_description"), rs.getString("achievement_photo"));
    }
}
