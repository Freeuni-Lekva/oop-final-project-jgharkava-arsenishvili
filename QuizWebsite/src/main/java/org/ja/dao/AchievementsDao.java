package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Achievement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
create table achievements(
    achievement_id bigint primary key auto_increment,
    achievement_name varchar(64) unique not null,
    achievement_description text not null,
    achievement_photo mediumblob
);
 */
public class AchievementsDao {
    BasicDataSource dataSource;
    public AchievementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertAchievement(Achievement achievement) {
        String sql="INSERT INTO achievements (achievement_name, " +
                "achievement_description, achievement_photo) VALUES (?,?,?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, achievement.getAchievementName());
            ps.setString(2, achievement.getAchievementDescription());
            ps.setString(3, achievement.getAchievementPhoto());
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                achievement.setAchievementId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeAchievement(long id){
        String sql = "DELETE FROM achievements WHERE achievement_id="+id;
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Achievement getAchievement(long id){
        String sql="SELECT * FROM achievements WHERE achievement_id="+id;
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                Achievement achievement=new Achievement();
                achievement.setAchievementId(rs.getLong(1));
                achievement.setAchievementName(rs.getString(2));
                achievement.setAchievementDescription(rs.getString(3));
                achievement.setAchievementPhoto(rs.getString(4));
                return achievement;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
