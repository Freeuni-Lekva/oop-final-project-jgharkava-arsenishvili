package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Friendship;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    BasicDataSource dataSource;
    public FriendShipsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertFriendShip(Friendship friendship) {
        String sql="INSERT INTO friendships (first_user_id, " +
                "second_user_id, friendship_date, friendship_status) VALUES (?,?, ?, ?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, friendship.getFirstUserId());
            ps.setLong(2, friendship.getSecondUserId());
            ps.setTimestamp(3, friendship.getFriendshipDate());
            ps.setString(4, friendship.getFriendshipStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeFriendShip(long first_id, long second_id) {
        String sql="DELETE FROM friendships WHERE first_user_id = ? AND second_user_id = ?";
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, first_id);
            ps.setLong(2, second_id);
            ps.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    ArrayList<Friendship> getFriends(long user_id) {
        String sql="SELECT * FROM friendships where friendship_status='friends' " +
                "AND (first_user_id="+user_id+" OR second_user_id="+user_id+")";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            ArrayList<Friendship> friendships=new ArrayList<>();
            while(rs.next()){
                Friendship friendship=new Friendship();
                friendship.setFirstUserId(rs.getLong(1));
                friendship.setSecondUserId(rs.getLong(2));
                friendship.setFriendshipDate(rs.getTimestamp(3));
                friendship.setFriendshipStatus(rs.getString(4));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    //its assumede that when A send friend request to B,
    //when this 'friendship' is added to the database, A is the firstuser and B is the secondUser
    ArrayList<Friendship> getFriendRequests(long user_id) {
        String sql="SELECT * FROM friendships where friendship_status='pending' " +
                "AND second_user_id="+user_id;
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            ArrayList<Friendship> friendships=new ArrayList<>();
            while(rs.next()){
                Friendship friendship=new Friendship();
                friendship.setFirstUserId(rs.getLong(1));
                friendship.setSecondUserId(rs.getLong(2));
                friendship.setFriendshipDate(rs.getTimestamp(3));
                friendship.setFriendshipStatus(rs.getString(4));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
