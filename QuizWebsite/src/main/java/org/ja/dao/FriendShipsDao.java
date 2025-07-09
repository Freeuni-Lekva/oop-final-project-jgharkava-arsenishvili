package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Friendship;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing friendships in the database.
 */
public class FriendShipsDao {
    private final BasicDataSource dataSource;

    /**
     * Constructs a FriendShipsDao with the given data source.
     * @param dataSource the data source to use
     */
    public FriendShipsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Sends a friend request from one user to another. If a request already exists, throws a RuntimeException.
     * Sets the status to "pending" and initializes the friendship date.
     *
     * @param friendship the friendship to insert
     * @return true if the request was successfully inserted
     */
    public boolean insertFriendRequest(Friendship friendship) {
        String sql = "INSERT INTO friendships (first_user_id, second_user_id) VALUES (?, ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) return false;
            
            friendship.setFriendshipStatus("pending");
            setFriendshipDate(friendship);

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to send friend request", e);
        }
    }


    /**
     * Retrieves a friendship record between two users, regardless of order.
     *
     * @param firstUserId first user
     * @param secondUserId second user
     * @return the Friendship object
     * @throws RuntimeException if no friendship is found or a database error occurs
     */
    public Friendship getFriendshipByIds(long firstUserId, long secondUserId){
        String sql = "SELECT * FROM friendships WHERE (first_user_id = ? AND second_user_id = ?) OR (first_user_id = ? AND second_user_id = ?)";

        try (Connection c= dataSource.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){

            ps.setLong(1, firstUserId);
            ps.setLong(2, secondUserId);
            ps.setLong(3, secondUserId);
            ps.setLong(4, firstUserId);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    return retrieveFriendship(rs);
                } else {
                    throw new RuntimeException("No friendship found between users " + firstUserId + " and " + secondUserId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching friendship between user " + firstUserId + " and user " + secondUserId, e);
        }
    }


    /**
     * Accepts a pending friend request. Sets status to "friends".
     *
     * @param friendship the friendship to accept
     * @return true if the friendship was successfully updated
     */
    public boolean acceptFriendRequest(Friendship friendship){
        String sql = "UPDATE friendships SET friendship_status = 'friends' WHERE first_user_id = ? and second_user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) return false;

            friendship.setFriendshipStatus("friends");
            setFriendshipDate(friendship);

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error accepting friendship request in database", e);
        }
    }


    /**
     * Removes a friendship regardless of direction (first/second user).
     *
     * @param friendship the friendship to remove
     * @return true if the friendship was deleted
     */
    public boolean removeFriendShip(Friendship friendship) {
        String sql = "DELETE FROM friendships WHERE (first_user_id = ? AND second_user_id = ?) " +
                "OR (first_user_id = ? AND second_user_id = ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());
            ps.setLong(3, friendship.getSecondUserId());
            ps.setLong(4, friendship.getFirstUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error removing friendship from database", e);
        }
    }


    /**
     * Retrieves all accepted friends of a user.
     *
     * @param userId the user ID
     * @return a list of friendships (with status 'friends')
     */
    public List<Friendship> getFriends(long userId) {
        List<Friendship> friendships = new ArrayList<>();

        String sql = "SELECT * FROM friendships where friendship_status = 'friends' " +
                "AND (first_user_id = ? OR second_user_id = ?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, userId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    friendships.add(retrieveFriendship(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying friends list from database", e);
        }

        return friendships;
    }


    /**
     * Retrieves all pending friend requests where the given user is the recipient.
     *
     * @param userId the recipient user ID
     * @return list of pending friendships
     */
    public List<Friendship> getFriendRequests(long userId) {
        List<Friendship> friendships = new ArrayList<>();

        String sql = "SELECT * FROM friendships where friendship_status = 'pending' AND second_user_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    friendships.add(retrieveFriendship(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving friend requests from database", e);
        }

        return friendships;
    }


    // --- Helper Methods ---


    /**
     * Helper method to populate friendship date from DB into object.
     */
    private void setFriendshipDate(Friendship friendship) throws SQLException {
        String selectSql = "SELECT friendship_date FROM friendships WHERE first_user_id = ? AND second_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement p = conn.prepareStatement(selectSql)) {

            p.setLong(1, friendship.getFirstUserId());
            p.setLong(2, friendship.getSecondUserId());

            try (ResultSet rs = p.executeQuery()) {
                if (rs.next()) {
                    friendship.setFriendshipDate(rs.getTimestamp("friendship_date"));
                }
            }
        }
    }


    /**
     * Helper method to extract a Friendship object from the current ResultSet row.
     *
     * @param rs the ResultSet
     * @return the Friendship object
     */
    private Friendship retrieveFriendship(ResultSet rs) throws SQLException {
        return new Friendship(rs.getLong("first_user_id"), rs.getLong("second_user_id"),
                rs.getTimestamp("friendship_date"), rs.getString("friendship_status"));
    }
}

