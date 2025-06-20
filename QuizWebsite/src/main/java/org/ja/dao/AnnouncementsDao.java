package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Announcement;
import org.ja.model.OtherObjects.History;
import org.ja.model.OtherObjects.QuizRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
create table announcements(
    announcement_id bigint primary key auto_increment,
    administrator_id bigint,
    announcement_text text not null,
    creation_date timestamp default current_timestamp,

    foreign key (administrator_id) references users(user_id) on delete cascade
);
 */

public class AnnouncementsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public AnnouncementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertAnnouncement(Announcement announcement){
        if(contains(announcement)){
            return;
        }
        String sql = "INSERT into announcements (administrator_id, announcement_text) VALUES (?, ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, announcement.getAdministratorId());
            ps.setString(2, announcement.getAnnouncementText());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    cnt++;
                    announcement.setCreationDate(rs.getTimestamp("creation_date"));
                    announcement.setAnnouncementId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting announcement into database", e);
        }
    }

    public ArrayList<Announcement> getAllAnnouncements(){
        ArrayList<Announcement> announcements = new ArrayList<>();

        String sql = "SELECT * FROM announcements";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){

            while(rs.next()) {
                announcements.add(retrieveAnnouncement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying announcements from database", e);
        }

        return announcements;
    }

    public ArrayList<Announcement> getAnnouncementsSortedByCreationDate(){
        ArrayList<Announcement> announcements = new ArrayList<>();

        String sql = "SELECT * FROM announcements ORDER BY creation_date DESC";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){

            while(rs.next()) {
                announcements.add(retrieveAnnouncement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying announcements from database", e);
        }

        return announcements;
    }

    public void removeAnnouncement(long announcementId){
        if(!contains(announcementId)){
            return;
        }
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, announcementId);

            ps.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing announcement from database", e);
        }
    }
    public boolean contains(Announcement a){
        if(a==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM announcements WHERE announcement_id=?" +
                "AND administrator_id=? AND announcement_text=? AND creation_date=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, a.getAnnouncementId());
            ps.setLong(2,a.getAdministratorId());
            ps.setString(3, a.getAnnouncementText());
            ps.setTimestamp(4,a.getCreationDate());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public boolean contains(long aid){
        if(aid<0||aid>cnt){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM announcements WHERE announcement_id=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aid);
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
    private Announcement retrieveAnnouncement(ResultSet rs) throws SQLException {
        return new Announcement(rs.getLong("announcement_id"), rs.getLong("administrator_id"),
                rs.getString("announcement_text"), rs.getTimestamp("creation_date"));
    }
}
