package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.CategoriesAndTags.Category;
import org.ja.model.Filters.Filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class CategoriesDao {
    private BasicDataSource dataSource;
    public CategoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void addCategory(Category category) {
        String sql="INSERT INTO categories (category_name) VALUES (?);";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, category.getCategoryName());

            preparedStatement.execute();
            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                category.setCategoryId(newId); // if you want to store it in your object
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeCategory(Category category) {
        String sql="DELETE FROM categories WHERE category_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql);
            preparedStatement.setLong(1, category.getCategoryId());
            preparedStatement.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Category getCategoryById(long id) {
        String sql="SELECT * FROM categories WHERE category_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                long newId = rs.getLong(1);
                String categoryName = rs.getString(2);
                return new Category(newId,categoryName);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }
    public Category getCategoryByName(String categoryName) {
        String sql="SELECT * FROM categories WHERE category_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setString(1, categoryName);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                long newId = rs.getLong(1);
                String newName = rs.getString(2);
                return new Category(newId,newName);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return null;
    }
    /*public ArrayList<Category> filterCategories(Filter filter) {
        String sql="SELECT * FROM categories WHERE "+filter.toString();
        ArrayList<Category> categories = new ArrayList<>();
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql);
            ResultSet rs=preparedStatement.executeQuery();
            while(rs.next()){
                Category newCategory=new Category(rs.getLong("category_id"),
                        rs.getString("category_name"));
                categories.add(newCategory);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return categories;
    }*/


}
