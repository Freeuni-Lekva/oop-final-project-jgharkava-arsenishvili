package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Announcement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for announcement-specific operations.
 */
public class AnnouncementsDao {
    private final BasicDataSource dataSource;

    /**
     * Constructs an AnnouncementsDao with the given data source.
     *
     * @param dataSource the BasicDataSource for database connections
     */
    public AnnouncementsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new announcement into the database.
     * The generated announcement ID and creation date will be set in the provided Announcement object.
     *
     * @param announcement the Announcement object to insert
     * @return the generated announcement ID
     * @throws RuntimeException if a database error occurs or insertion fails
     */
    public boolean insertAnnouncement(Announcement announcement){
        String sql = "INSERT INTO announcements (administrator_id, announcement_text) VALUES (?, ?) ";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, announcement.getAdministratorId());
            ps.setString(2, announcement.getAnnouncementText());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Inserting announcement failed, no rows affected.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) {
                    long announcementId = rs.getLong(1);

                    announcement.setAnnouncementId(announcementId);
                    announcement.setCreationDate(fetchCreationDate(c, announcementId));
                } else {
                    throw new SQLException("Inserting announcement failed, no ID obtained.");
                }
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting announcement into database", e);
        }
    }


    /**
     * Retrieves all announcements ordered by creation date descending.
     *
     * @return a list of announcements, newest first
     * @throws RuntimeException if a database error occurs
     */
    public List<Announcement> getAllAnnouncements(){
        ArrayList<Announcement> announcements = new ArrayList<>();

        String sql = "SELECT * FROM announcements ORDER BY creation_date DESC";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){

            while (rs.next()) {
                announcements.add(retrieveAnnouncement(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying announcements from database", e);
        }

        return announcements;
    }


    // --- Helper Methods ---


    /**
     * Retrieves the creation date of an announcement by its ID.
     *
     * @param c the active database connection
     * @param announcementId the announcement ID
     * @return the creation timestamp
     * @throws SQLException if the announcement does not exist or a database error occurs
     */
    private Timestamp fetchCreationDate(Connection c, long announcementId) throws SQLException {
        String sql = "SELECT creation_date FROM announcements WHERE announcement_id = ?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, announcementId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("creation_date");
                }
                throw new SQLException("Creation date not found for announcement ID: " + announcementId);
            }
        }
    }

    /**
     * Helper to map a ResultSet row to an Announcement object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return the Announcement object
     * @throws SQLException if a database access error occurs
     */
    private Announcement retrieveAnnouncement(ResultSet rs) throws SQLException {
        return new Announcement(rs.getLong("announcement_id"), rs.getLong("administrator_id"),
                rs.getString("announcement_text"), rs.getTimestamp("creation_date"));
    }
}
