package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Achievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
create table achievements(
    achievement_id bigint primary key auto_increment,
    achievement_name varchar(64) unique not null,
    achievement_description text not null,
    achievement_photo varchar(256)
);
 */
public class AchievementsDao {
    private final BasicDataSource dataSource;

    public AchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertAchievement(Achievement achievement){
        String sql = "INSERT INTO achievements (achievement_name, " +
                "achievement_description, achievement_photo) VALUES (?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, achievement.getAchievementName());
            ps.setString(2, achievement.getAchievementDescription());
            ps.setString(3, achievement.getAchievementPhoto());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next())
                    achievement.setAchievementId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting achievement into database", e);
        }
    }

    public void removeAchievement(long id){
        String sql = "DELETE FROM achievements WHERE achievement_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing Achievement from database", e);
        }
    }

    public Achievement getAchievement(long id){
        String sql = "SELECT * FROM achievements WHERE achievement_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveAchievement(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying achievement from database", e);
        }

        return null;
    }

    private Achievement retrieveAchievement(ResultSet rs) throws SQLException {
        return new Achievement(rs.getLong("achievement_id"), rs.getString("achievement_name"),
                rs.getString("achievement_description"), rs.getString("achievement_photo"));
    }
}
