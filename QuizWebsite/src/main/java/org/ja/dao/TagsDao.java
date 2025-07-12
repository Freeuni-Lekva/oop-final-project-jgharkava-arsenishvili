package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.Tag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for performing operations on the 'tags' table.
 */
public class TagsDao {
    private final BasicDataSource dataSource;


    /**
     * Constructs a new TagsDao using the given data source.
     * @param dataSource the database connection pool
     */
    public TagsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new tag into the database. Sets the tag ID in the Tag object.
     * If the tag name already exists, a RuntimeException is thrown.
     *
     * @param tag the tag to insert
     * @return true if inserted successfully
     * @throws RuntimeException if tag name already exists or SQL error occurs
     */
    public boolean insertTag(Tag tag) {
        String sql = "INSERT INTO tags (tag_name) VALUES (?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, tag.getTagName());

            if (preparedStatement.executeUpdate() == 0){
                return false;
            }

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if (keys.next()) {
                    long newId = keys.getLong(1);
                    tag.setTagId(newId);
                } else {
                    throw new RuntimeException("Inserting tag failed, no ID obtained.");
                }
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting tag into database", e);
        }
    }


    /**
     * Removes a tag from the database using its ID.
     * @param tag the tag to remove
     * @return true if deleted successfully
     */
    public boolean removeTag(Tag tag) {
        String sql = "DELETE FROM tags WHERE tag_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setLong(1, tag.getTagId());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing tag from database", e);
        }
    }


    /**
     * Retrieves a tag by its ID.
     * @param id the ID of the tag
     * @return the tag if found, otherwise null
     */
    public Tag getTagById (long id) {
        String sql = "SELECT * FROM tags WHERE tag_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveTag(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying tag from by id database", e);
        }
        return null;
    }


    /**
     * Retrieves a tag by its name.
     * @param name the name of the tag
     * @return the tag if found, otherwise null
     */
    public Tag getTagByName(String name) {
        String sql = "SELECT * FROM tags WHERE tag_name = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveTag(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying tag from by id database", e);
        }
        return null;
    }


    /**
     * Retrieves all tags in the database.
     *
     * @param limit the maximum number of tags to return
     * @return a list of all tags
     */
    public List<Tag> getAllTags(int limit){
        List<Tag> tags = new ArrayList<>();

        String sql = "SELECT * FROM TAGS LIMIT ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, limit);

             try (ResultSet rs = ps.executeQuery()){
                 while (rs.next()) {
                     tags.add(retrieveTag(rs));
                 }
             }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying all tags", e);
        }

        return tags;
    }


    // --- Helper Methods ---

    /**
     * Helper method to convert a ResultSet row into a Tag object.
     * @param rs the ResultSet positioned at the desired row
     * @return the Tag object
     * @throws SQLException if an error occurs while reading from ResultSet
     */
    private Tag retrieveTag(ResultSet rs) throws SQLException {
        return new Tag(rs.getLong("tag_id"), rs.getString("tag_name"));
    }


}
