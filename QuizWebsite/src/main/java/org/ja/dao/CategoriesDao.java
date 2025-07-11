package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.Category;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing categories in the quiz system.
 */
public class CategoriesDao {
    private final BasicDataSource dataSource;

    /**
     * Constructs a new CategoriesDao with the specified data source.
     *
     * @param dataSource the database connection pool to use for queries
     */
    public CategoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new category into the database.
     * Sets the generated ID on the passed {@link Category} object if successful.
     *
     * @param category the Category object to insert (with name set)
     * @return true if the category was inserted and ID was generated; false otherwise
     * @throws RuntimeException if an SQL error occurs or generated ID is not returned
     */
    public boolean insertCategory(Category category) {
        String sql = "INSERT INTO categories (category_name) VALUES (?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, category.getCategoryName());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) return false;

            try (ResultSet keys = preparedStatement.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setCategoryId(keys.getLong(1));
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error adding category to database", e);
        }
    }


    /**
     * Retrieves a category by its unique ID.
     *
     * @param id the ID of the category to retrieve
     * @return the Category object if found, or null otherwise
     */
    public Category getCategoryById(long id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return retrieveCategory(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error querying category by id from database", e);
        }
    }


    /**
     * Retrieves a category by its unique name.
     *
     * @param name the name of the category to retrieve
     * @return the Category with the specified name, or null otherwise
     */
    public Category getCategoryByName(String name) {
        String sql = "SELECT * FROM categories WHERE category_name = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return retrieveCategory(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error querying category by name from database", e);
        }
    }


    /**
     * Retrieves a limited number of categories from the database.
     *
     * @param limit the maximum number of categories to retrieve
     * @return a list of Category objects, up to the specified limit; empty list if none exist
     * @throws RuntimeException if a database error occurs
     */
    public List<Category> getAllCategories(int limit){
        List<Category> categories = new ArrayList<>();

        String sql = "SELECT * FROM categories LIMIT ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setInt(1, limit);

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



    // --- Helper Methods ---


    /**
     * Helper method to map a ResultSet row to a Category object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return a Category object populated with data from the current row
     * @throws SQLException if an error occurs accessing the ResultSet
     */
    private Category retrieveCategory(ResultSet rs) throws SQLException {
        return new Category(rs.getLong("category_id"), rs.getString("category_name"));
    }

}
