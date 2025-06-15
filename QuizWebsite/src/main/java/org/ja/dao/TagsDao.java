package org.ja.dao;


import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.CategoriesAndTags.Tag;
import org.ja.model.Filters.Filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TagsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public TagsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertTag(Tag tag) {
        if(containsTag(tag.getTagName())) {
            return;
        }
        String sql = "INSERT INTO tags (tag_name) VALUES (?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, tag.getTagName());

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if (keys.next()) {
                    cnt++;
                    long newId = keys.getLong(1);
                    tag.setTagId(newId); // if you want to store it in your object
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting tag into database", e);
        }
    }

    public void removeTag(Tag tag) {
        if(!containsTag(tag.getTagName())) {
            return;
        }
        String sql = "DELETE FROM tags WHERE tag_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql)){

            preparedStatement.setLong(1, tag.getTagId());
            preparedStatement.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing tag from database", e);
        }
    }

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

    public Tag getTagByName(String tagName) {
        String sql="SELECT * FROM tags WHERE tag_name = ?";
        try (Connection c=dataSource.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){

            ps.setString(1, tagName);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveTag(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying tag from by name database", e);
        }
        return null;
    }
    public boolean containsTag(String name) {
        String sql = "SELECT COUNT(*) FROM tags WHERE tag_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public boolean containsTag(long id) {
        String sql = "SELECT COUNT(*) FROM tags WHERE tag_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public long getCount() {
        return cnt;
    }
    /*public ArrayList<Tag> filterCategories(Filter filter) {
        String sql="SELECT * FROM categories WHERE "+filter.toString();
        ArrayList<Tag> tags = new ArrayList<>();
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql);
            ResultSet rs=preparedStatement.executeQuery();
            while(rs.next()){
                Tag newTag=new Tag(rs.getLong("tag_id"),
                        rs.getString("tag_name"));
                tags.add(newTag);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tags;
    }*/

    private Tag retrieveTag(ResultSet rs) throws SQLException {
        return new Tag(rs.getLong("tag_id"), rs.getString("tag_name"));
    }


}
