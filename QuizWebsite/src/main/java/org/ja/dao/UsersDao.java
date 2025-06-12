package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.user.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsersDao {
    private final BasicDataSource dataSource;

    public UsersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO users ( password_hashed, username, registration_date, user_photo, user_status) " +
                "VALUES (?,?, ?, ?, ?);";
        try (Connection c=dataSource.getConnection();
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, user.getPasswordHashed());
            preparedStatement.setString(2, user.getUserName());
            preparedStatement.setString(3, user.getRegistrDate());
            preparedStatement.setString(4, user.getPhoto());
            preparedStatement.setString(5, user.getStatus());

            preparedStatement.executeUpdate();

            ResultSet keys = preparedStatement.getGeneratedKeys();
            if (keys.next()) {
                long newId = keys.getLong(1);
                user.setId(newId); // if you want to store it in your object
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user into database", e);
        }
    }

    public void removeUserById(long id) {
        String sql = "DELETE FROM users WHERE user_id=?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user by id from database", e);
        }
    }

    public void removeUserByName(String name) {
        String sql = "DELETE FROM users WHERE user_name=?";

        try(Connection c=dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user by name from database", e);
        }
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id=?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement st = c.prepareStatement(sql)){

            st.setLong(1, id);
            ResultSet rs = st.executeQuery();

            if(rs.next())
                return retrieveUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error querying user by id from database", e);
        }
        return null;
    }

    private User getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        try (Connection c=dataSource.getConnection();
            PreparedStatement st=c.prepareStatement(sql)){

            st.setString(1, username);
            ResultSet rs = st.executeQuery();

            if(rs.next())
                return retrieveUser(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Error querying user by username from database", e);
        }
        return null;
    }

    private ArrayList<User> getUsersByFilter(Filter filter) {
        String sql="SELECT * FROM users WHERE " + filter.toString();

        ArrayList<User> users = new ArrayList<>();

        try (Connection c=dataSource.getConnection();
            PreparedStatement st=c.prepareStatement(sql)){

            ResultSet rs = st.executeQuery();
            while(rs.next())
                users.add(retrieveUser(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error querying user by filter from database", e);
        }

        return users;
    }

    private User retrieveUser(ResultSet rs) throws SQLException {
        return new User(rs.getLong("user_id"), rs.getString("username"),
                rs.getString("registration_date"), rs.getString("user_photo"),
                rs.getString("user_status"), rs.getString("password_hashed"));
    }
}
