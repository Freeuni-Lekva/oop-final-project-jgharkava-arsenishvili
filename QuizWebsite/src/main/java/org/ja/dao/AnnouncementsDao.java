package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Announcement;

import java.sql.*;
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
    private long cnt = 0;

    public AnnouncementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertAnnouncement(Announcement announcement){
        String sql = "INSERT INTO announcements (administrator_id, announcement_text) VALUES (?, ?) ";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, announcement.getAdministratorId());
            ps.setString(2, announcement.getAnnouncementText());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) {
                    cnt++;
                    long announcementId = rs.getLong(1);
                    announcement.setAnnouncementId(announcementId);

                    String s = "SELECT creation_date FROM announcements where announcement_id = ?";

                    try (PreparedStatement preparedStatement = c.prepareStatement(s)){
                        preparedStatement.setLong(1, announcementId);

                        try (ResultSet r = preparedStatement.executeQuery()) {
                            if (r.next())
                                announcement.setCreationDate(r.getTimestamp("creation_date"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting announcement into database", e);
        }
    }

    ///  returns announcements sorted by creation date
    public ArrayList<Announcement> getAllAnnouncements(){
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
        String sql = "DELETE FROM announcements WHERE announcement_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, announcementId);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing announcement from database", e);
        }
    }

    public boolean contains(Announcement a){
        if(a == null){
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

    public boolean contains(long aid){
        String sql = "SELECT COUNT(*) FROM announcements WHERE announcement_id=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, aid);

            try (ResultSet rs = ps.executeQuery()) {
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
        return cnt;
    }

    private Announcement retrieveAnnouncement(ResultSet rs) throws SQLException {
        return new Announcement(rs.getLong("announcement_id"), rs.getLong("administrator_id"),
                rs.getString("announcement_text"), rs.getTimestamp("creation_date"));
    }
}
