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

    public FriendShipsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertFriendShip(Friendship friendship) {
        String sql = "INSERT INTO friendships (first_user_id, " +
                "second_user_id, friendship_date, friendship_status) VALUES (?,?, ?, ?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());
            ps.setTimestamp(3, friendship.getFriendshipDate());
            ps.setString(4, friendship.getFriendshipStatus());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting friendship into database", e);
        }
    }

    public void removeFriendShip(long first_id, long second_id) {
        String sql = "DELETE FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, first_id);
            ps.setLong(2, second_id);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Error removing friendship from database", e);
        }
    }

    public ArrayList<Friendship> getFriends(long user_id) {
        ArrayList<Friendship> friendships = new ArrayList<>();

        String sql = "SELECT * FROM friendships where friendship_status = 'friends' " +
                "AND (first_user_id=" + user_id + " OR second_user_id=" + user_id + ")";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();

            while(rs.next())
                friendships.add(retrieveFriendship(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error querying friends list from database", e);
        }

        return friendships;
    }

    //its assumed that when A send friend request to B,
    //when this 'friendship' is added to the database, A is the firstUser and B is the secondUser
    ArrayList<Friendship> getFriendRequests(long user_id) {
        ArrayList<Friendship> friendships = new ArrayList<>();

        String sql = "SELECT * FROM friendships where friendship_status='pending' " +
                "AND second_user_id="+user_id;
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ResultSet rs = ps.executeQuery();
            while(rs.next())
                friendships.add(retrieveFriendship(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving friend requests from database", e);
        }

        return friendships;
    }

    private Friendship retrieveFriendship(ResultSet rs) throws SQLException {
        return new Friendship(rs.getLong("first_user_id"), rs.getLong("second_user_id"),
                rs.getTimestamp("friendship_date"), rs.getString("friendship_status"));
    }
}
