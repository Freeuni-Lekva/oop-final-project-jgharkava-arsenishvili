package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Friendship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
create table friendships(
    first_user_id bigint not null,
    second_user_id bigint not null,
    friendship_date timestamp default current_timestamp,
    friendship_status enum('pending', 'friends'),

    primary key (first_user_id, second_user_id),
    foreign key (first_user_id) references users(user_id) on delete cascade,
    foreign key (second_user_id) references users(user_id) on delete cascade
);
 */
public class FriendShipsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public FriendShipsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    //TO DELETE
    public void insertFriendShip(Friendship friendship) {
        if(contains(friendship)) {
            return;
        }
        String sql = "INSERT INTO friendships (first_user_id, second_user_id, friendship_status) VALUES (?, ?, ?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());
            ps.setString(3, friendship.getFriendshipStatus());
            ps.executeUpdate();

            cnt++;

            String s = "SELECT friendship_date FROM friendships " +
                    "where first_user_id = ? AND second_user_id = ?";

            try (PreparedStatement preparedStatement = c.prepareStatement(s)){
                preparedStatement.setLong(1, friendship.getFirstUserId());
                preparedStatement.setLong(2, friendship.getSecondUserId());

                try (ResultSet r = preparedStatement.executeQuery()) {
                    if (r.next())
                        friendship.setFriendshipDate(r.getTimestamp("friendship_date"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting friendship into database", e);
        }
    }

    public void insertFriendRequest(Friendship friendship){
        if(contains(friendship)){
            return;
        }
        if(contains(friendship.getFirstUserId(), friendship.getSecondUserId())) {
            acceptFriendRequest(friendship);
        }
        String sql = "INSERT INTO friendships (first_user_id, second_user_id) VALUES (?, ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                friendship.setFriendshipDate(rs.getTimestamp("friendship_date"));
                friendship.setFriendshipStatus("pending");
                cnt++;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting friendship into database", e);
        }
    }

    public void acceptFriendRequest(Friendship friendship){

        String sql = "UPDATE friendships SET friendship_status = 'friends' WHERE first_user_id = ? and second_user_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());

            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                friendship.setFriendshipStatus("friends");
                friendship.setFriendshipDate(rs.getTimestamp("friendship_date"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error accepting friendship request in database", e);
        }
    }

    public void removeFriendShip(Friendship f) {
        if(!contains(f)) {
            return;
        }

        String sql = "DELETE FROM friendships WHERE first_user_id = ? AND second_user_id = ?" +
                "AND friendship_status = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, f.getFirstUserId());
            ps.setLong(2, f.getSecondUserId());
            ps.setString(3, f.getFriendshipStatus());
            ps.executeUpdate();
            cnt--;
        } catch (SQLException e){
            throw new RuntimeException("Error removing friendship from database", e);
        }
    }
    public void removeFriendShip(long first_id, long second_id) {
        if(!contains(first_id, second_id)) {
            return;
        }

        String sql = "DELETE FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, first_id);
            ps.setLong(2, second_id);

            ps.executeUpdate();
            cnt--;
        } catch (SQLException e){
            throw new RuntimeException("Error removing friendship from database", e);
        }
    }

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

    //its assumed that when A send friend request to B,
    //when this 'friendship' is added to the database, A is the firstUser and B is the secondUser
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
        if(f==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM friendships WHERE first_user_id = ? AND second_user_id = ?" +
                "AND friendship_status=? ";
        try(Connection c= dataSource.getConnection();
        PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1, f.getFirstUserId());
            ps.setLong(2, f.getSecondUserId());
            ps.setString(3, f.getFriendshipStatus());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public boolean contains(long first_id, long second_id) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
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
