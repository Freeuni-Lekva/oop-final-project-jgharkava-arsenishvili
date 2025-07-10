package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.user.User;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object for performing operations on the 'users' table.
 */
public class UsersDao {
    private final BasicDataSource dataSource;

    /**
     * Constructs a UsersDao with the provided DataSource.
     *
     * @param dataSource the data source used to obtain DB connections
     */
    public UsersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new user into the database.
     * If a user with the same username already exists, a RuntimeException is thrown.
     * The generated user ID and registration date are set on the user object.
     *
     * @param user the user object to insert
     * @return true if insertion was successful, false otherwise
     * @throws SQLException if a database access error occurs
     * @throws RuntimeException if user cannot be inserted or no ID is returned
     */
    public boolean insertUser(User user) throws SQLException {
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

            if (preparedStatement.executeUpdate() == 0){
                return false;
            }

            try (ResultSet keys = preparedStatement.getGeneratedKeys()){
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                    loadRegistrationDate(user, c);
                } else {
                    throw new RuntimeException("Inserted user into database but no ID generated");
                }
            }
            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting user into database", e);
        }
    }


    /**
     * Removes a user from the database by their ID.
     *
     * @param id the user ID to delete
     * @return true if a user was deleted, false otherwise
     */
    public boolean removeUserById(long id) {
        String sql = "DELETE FROM users WHERE user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {
            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user by id from database", e);
        }
    }


    /**
     * Removes a user from the database by their username.
     *
     * @param name the username to delete
     * @return true if a user was deleted, false otherwise
     */
    public boolean removeUserByName(String name) {
        String sql = "DELETE FROM users WHERE username = ?";

        try(Connection c=dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setString(1, name);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user by name from database", e);
        }
    }

    public void updatePhoto(String photo, long id) {
        String sql = "UPDATE users SET user_photo = ? WHERE user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement preparedStatement = c.prepareStatement(sql)) {

            preparedStatement.setString(1, photo);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating user photo", e);
        }
    }


    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a User object if found, or null otherwise
     */
    public User getUserById(long id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement st = c.prepareStatement(sql)){

            st.setLong(1, id);

            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return retrieveUser(rs);
                }
            }

        } catch (SQLException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Error querying user by id from database", e);
        }
        return null;
    }


    /**
     * Retrieves a user by their username.
     *
     * @param username the username to search for
     * @return a User object if found, or null otherwise
     */
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


    // --- Helper Methods ---


    /**
     * Loads the registration date for a given user ID and sets it on the User object.
     *
     * @param user the user object to update
     * @param connection the active SQL connection
     * @throws SQLException if a database error occurs
     */
    public void loadRegistrationDate(User user, Connection connection) throws SQLException {
        String sql = "SELECT registration_date FROM users WHERE user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user.setRegistrationDate(rs.getTimestamp("registration_date"));
                }
            }
        }
    }


    /**
     * Helper method to convert a ResultSet row into a User object.
     * @param rs the ResultSet positioned at the desired row
     * @return the User object
     * @throws SQLException if an error occurs while reading from ResultSet
     */
    private User retrieveUser(ResultSet rs) throws SQLException, NoSuchAlgorithmException {
        return new User(rs.getLong("user_id"), rs.getString("username"),
                rs.getString("password_hashed"), rs.getString("salt"),
                rs.getTimestamp("registration_date"), rs.getString("user_photo"),
                rs.getString("user_status"));
    }
}
