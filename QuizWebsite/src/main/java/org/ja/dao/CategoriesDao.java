package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.CategoriesAndTags.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CategoriesDao {
    private final BasicDataSource dataSource;
    private long cnt = 0;

    public CategoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// throws RuntimeException if category_name is already in use
    public void insertCategory(Category category) {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, category.getCategoryName());

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    cnt++;
                    long newId = keys.getLong(1);
                    category.setCategoryId(newId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding category to database", e);
        }
    }

    public void removeCategory(Category category) {
        String sql = "DELETE FROM categories WHERE category_id = ?";

        try(Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setLong(1, category.getCategoryId());

            if(preparedStatement.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing category from database", e);
        }
    }

    ///  returns null if category is not present in table
    public Category getCategoryById(long id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveCategory(rs);
            }

        } catch (SQLException e){
            throw new RuntimeException("Error querying category by id from database", e);
        }
        return null;
    }

    public ArrayList<Category> getAllCategories(){
        ArrayList<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM categories";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()) {
                    categories.add(retrieveCategory(rs));
                }
            }

        } catch (SQLException e){
            throw new RuntimeException("Error querying categories  from database", e);
        }

        return categories;
    }

    ///  returns null if category is not present in table
    public Category getCategoryByName(String categoryName) {
        String sql = "SELECT * FROM categories WHERE category_name=?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1, categoryName);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveCategory(rs);
            }

        }catch (SQLException e){
            throw new RuntimeException("Error querying category by name from database", e);
        }

        return null;
    }

    private Category retrieveCategory(ResultSet rs) throws SQLException {
        return new Category(rs.getLong("category_id"), rs.getString("category_name"));
    }

    public long getCount(){
        return cnt;
    }

}
