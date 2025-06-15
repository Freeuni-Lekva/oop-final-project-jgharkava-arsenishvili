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
    private long cnt=0;
    public AchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertAchievement(Achievement achievement){
        if(contains(achievement)){
            return;
        }
        String sql = "INSERT INTO achievements (achievement_name, " +
                "achievement_description, achievement_photo) VALUES (?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, achievement.getAchievementName());
            ps.setString(2, achievement.getAchievementDescription());
            ps.setString(3, achievement.getAchievementPhoto());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()){
                    cnt++;
                    achievement.setAchievementId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting achievement into database", e);
        }
    }

    public void removeAchievement(long id){
        if(getAchievement(id)==null){
            return;
        }
        String sql = "DELETE FROM achievements WHERE achievement_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            ps.executeUpdate();
            cnt--;
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
    public boolean contains(Achievement a){
        if(a==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM achievements WHERE achievement_id = ? AND achievement_name=?" +
                "AND achievement_description=? AND achievement_photo = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, a.getAchievementId());
            ps.setString(2, a.getAchievementName());
            ps.setString(3, a.getAchievementDescription());
            ps.setString(4, a.getAchievementPhoto());
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
    private Achievement retrieveAchievement(ResultSet rs) throws SQLException {
        return new Achievement(rs.getLong("achievement_id"), rs.getString("achievement_name"),
                rs.getString("achievement_description"), rs.getString("achievement_photo"));
    }
}
