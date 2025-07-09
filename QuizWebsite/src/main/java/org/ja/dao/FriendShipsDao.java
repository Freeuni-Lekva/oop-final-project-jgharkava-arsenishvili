package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Friendship;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendShipsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public FriendShipsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// if first and second user already exists in table throws RuntimeException
    /// sets status to 'pending'
    public void insertFriendRequest(Friendship friendship) {
        String sql = "INSERT INTO friendships (first_user_id, second_user_id) VALUES (?, ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());

            ps.executeUpdate();

            cnt++;
            friendship.setFriendshipStatus("pending");

            retrieveFriendshipDate(friendship);

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting friendship into database", e);
        }
    }

    public Friendship getFriendshipByIds(long firstUserId, long secondUserId){
        String sql = "SELECT * FROM friendships WHERE (first_user_id = ? AND second_user_id = ?) OR (first_user_id = ? AND second_user_id = ?)";

        try(Connection c= dataSource.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1, firstUserId);
            ps.setLong(2, secondUserId);
            ps.setLong(3, secondUserId);
            ps.setLong(4, firstUserId);

            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()) {
                    Friendship f = new Friendship(firstUserId, secondUserId);
                    f.setFirstUserId(rs.getLong("first_user_id"));
                    f.setSecondUserId(rs.getLong("second_user_id"));
                    f.setFriendshipStatus(rs.getString("friendship_status"));
                    f.setFriendshipDate(rs.getTimestamp("friendship_date"));
                    return f;
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /// sets status to 'friends'
    public void acceptFriendRequest(Friendship friendship){
        String sql = "UPDATE friendships SET friendship_status = 'friends' WHERE first_user_id = ? and second_user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());

            ps.executeUpdate();
            friendship.setFriendshipStatus("friends");

            retrieveFriendshipDate(friendship);

        } catch (SQLException e) {
            throw new RuntimeException("Error accepting friendship request in database", e);
        }
    }

    private void retrieveFriendshipDate(Friendship friendship) throws SQLException {
        String selectSql = "SELECT friendship_date FROM friendships WHERE first_user_id = ? AND second_user_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement p = conn.prepareStatement(selectSql)) {

            p.setLong(1, friendship.getFirstUserId());
            p.setLong(2, friendship.getSecondUserId());

            try (ResultSet rs = p.executeQuery()) {
                if(rs.next()) {
                    friendship.setFriendshipDate(rs.getTimestamp("friendship_date"));
                }
            }
        }
    }

    /// just uses f.getFirstUserId and f.getSecondUserId, checks first user as both sender and recipient
    public void removeFriendShip(Friendship f) {
        String sql = "DELETE FROM friendships WHERE (first_user_id = ? AND second_user_id = ?) " +
                "OR (first_user_id = ? AND second_user_id = ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, f.getFirstUserId());
            ps.setLong(2, f.getSecondUserId());
            ps.setLong(3, f.getSecondUserId());
            ps.setLong(4, f.getFirstUserId());

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e){
            throw new RuntimeException("Error removing friendship from database", e);
        }
    }

    /// returns empty list if no friends found
    public ArrayList<Friendship> getFriends(long userId) {
        ArrayList<Friendship> friendships = new ArrayList<>();

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

    /// its assumed that when A send friend request to B,
    /// when this 'friendship' is added to the database, A is the firstUser and B is the secondUser
    /// returns empty list if no requests are found
    public ArrayList<Friendship> getFriendRequests(long userId) {
        ArrayList<Friendship> friendships = new ArrayList<>();

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

    public boolean contains(Friendship f){
        if(f == null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM friendships WHERE ((first_user_id = ? AND second_user_id = ?) OR (first_user_id = ? AND second_user_id = ?)) " +
                "AND friendship_status = ? ";
        try(Connection c= dataSource.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1, f.getFirstUserId());
            ps.setLong(2, f.getSecondUserId());
            ps.setLong(3, f.getSecondUserId());
            ps.setLong(4, f.getFirstUserId());
            ps.setString(5, f.getFriendshipStatus());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean contains(long first_id, long second_id) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE ((first_user_id = ? AND second_user_id = ?) OR (first_user_id = ? AND second_user_id = ?))";
        try(Connection c= dataSource.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1, first_id);
            ps.setLong(2, second_id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public long getCount(){
        return cnt;
    }

    private Friendship retrieveFriendship(ResultSet rs) throws SQLException {
        return new Friendship(rs.getLong("first_user_id"), rs.getLong("second_user_id"),
                rs.getTimestamp("friendship_date"), rs.getString("friendship_status"));
    }
}

