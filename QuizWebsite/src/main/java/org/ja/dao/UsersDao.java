package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.user.User;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.*;
import java.util.ArrayList;

public class UsersDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public UsersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertUser(User user) throws SQLException {
        if(containsUser(user.getUsername())){
            return;
        }
        String sql = "INSERT INTO users (password_hashed, username, user_photo, user_status, salt) " +
                "VALUES (?, ?, ?, ?, ?);";
        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement =
                    c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            preparedStatement.setString(1, user.getPasswordHashed());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPhoto());
            preparedStatement.setString(4, user.getStatus());
            preparedStatement.setString(5, user.getSalt());

            preparedStatement.executeUpdate();

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if (keys.next()) {
                    cnt++;
                    long userId = keys.getLong("user_id");
                    user.setId(userId);

                    String s = "SELECT registration_date FROM users where user_id = ?";

                    try (PreparedStatement ps = c.prepareStatement(s)){
                        ps.setLong(1, userId);

                        try (ResultSet r = ps.executeQuery()) {
                            if (r.next()) {
                                user.setRegistrationDate(r.getTimestamp("registration_date"));
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user into database", e);
        }
    }

    public void removeUserById(long id) {
        if(!containsUser(id)){
            return;
        }
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user by id from database", e);
        }
    }

    public void removeUserByName(String name) {
        if(!containsUser(name)){
            return;
        }
        String sql = "DELETE FROM users WHERE username = ?";

        try(Connection c=dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setString(1, name);

            preparedStatement.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user by name from database", e);
        }
    }

    public User getUserById(long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement st = c.prepareStatement(sql)){

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()){
                if (rs.next())
                    return retrieveUser(rs);
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error querying user by id from database", e);
        }
        return null;
    }
    public User getUserByUsername(String username) {
        String sql="SELECT * FROM users WHERE username=?";
        try (Connection c=dataSource.getConnection()){
            PreparedStatement st=c.prepareStatement(sql);
            st.setString(1, username);

            try (ResultSet rs = st.executeQuery()){
                if (rs.next())
                    return retrieveUser(rs);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

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

            try (ResultSet rs = st.executeQuery()){
                while (rs.next())
                    users.add(retrieveUser(rs));
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error querying user by filter from database", e);
        }

        return users;
    }
    public boolean containsUser(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public boolean containsUser(long id) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";

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

    public long getCount(){
        return cnt;
    }
    private User retrieveUser(ResultSet rs) throws SQLException, NoSuchAlgorithmException {
        return new User(rs.getLong("user_id"), rs.getString("username"),
                rs.getString("password_hashed"), rs.getString("salt"),
                rs.getTimestamp("registration_date"), rs.getString("user_photo"),
                rs.getString("user_status"));
    }
}
