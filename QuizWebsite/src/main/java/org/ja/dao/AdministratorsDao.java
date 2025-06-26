package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Announcement;
import org.ja.model.user.Administrator;

import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AdministratorsDao {
    private final BasicDataSource ds;

    public AdministratorsDao(BasicDataSource ds) {
        this.ds = ds;
    }

 /*   public ArrayList<Administrator> getAllAdministrators() {
        ArrayList<Administrator> administrators = new ArrayList<>();
        String sql = "SELECT user_id FROM users WHERE user_status = 'administrator'";
        try(Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql)){
            try (ResultSet rs = st.executeQuery()){
                while (rs.next()){
                    administrators.add(new Administrator(rs.getLong("id")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying administrators from database", e);
        }
        return administrators;
    }
*/
    public void promoteToAdministrator(long userId) {
        String sql = "UPDATE users SET user_status = 'administrator' WHERE user_id = ?";
        try(Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, userId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getUserCount(){
        String sql = "SELECT COUNT(*) FROM users";
        try(Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql);
            ResultSet rs = st.executeQuery()){
            if(rs.next()){
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public int getTakenQuizzesCount(){
        String sql = "SELECT COUNT(DISTINCT quiz_id) FROM history";
        try (Connection conn = ds.getConnection();
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public void removeUserById(long id) {
        try(Connection c = ds.getConnection()){
            try (PreparedStatement st1 = c.prepareStatement("DELETE FROM challenges WHERE sender_user_id = ? OR recipient_user_id = ?")) {
                st1.setLong(1, id);
                st1.setLong(2, id);
                st1.executeUpdate();
            }
    
            try (PreparedStatement st2 = c.prepareStatement("DELETE FROM messages WHERE sender_user_id = ? OR recipient_user_id = ?")) {
                st2.setLong(1, id);
                st2.setLong(2, id);
                st2.executeUpdate();
            }
    
            try (PreparedStatement st3 = c.prepareStatement("DELETE FROM history WHERE user_id = ?")) {
                st3.setLong(1, id);
                st3.executeUpdate();
            }
    
            try (PreparedStatement st4 = c.prepareStatement("DELETE FROM user_achievement WHERE user_id = ?")) {
                st4.setLong(1, id);
                st4.executeUpdate();
            }
    
            try (PreparedStatement st5 = c.prepareStatement("DELETE FROM friendships WHERE first_user_id = ? OR second_user_id = ?")) {
                st5.setLong(1, id);
                st5.setLong(2, id);
                st5.executeUpdate();
            }
    
            try (PreparedStatement st6 = c.prepareStatement("DELETE FROM quiz_rating WHERE user_id = ?")) {
                st6.setLong(1, id);
                st6.executeUpdate();
            }

            try (PreparedStatement st7 = c.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                st7.setLong(1, id);
                st7.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

/*    public void removeQuizById(long quizId) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";
        try(Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql)){
            st.setLong(1, quizId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }*/

    public void clearQuizHistory(long quizId) {
        String sql = "DELETE FROM history WHERE quiz_id = ?";
        try(Connection c = ds.getConnection();
            PreparedStatement st = c.prepareStatement(sql)) {
            st.setLong(1, quizId);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
