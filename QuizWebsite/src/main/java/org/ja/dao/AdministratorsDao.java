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
            throw new RuntimeException("Error promoting user to administrator", e);
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
            throw new RuntimeException("Error getting user count", e);
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
            throw new RuntimeException("Error getting count of taken quizzes", e);
        }
        return 0;
    }

    public void removeUserById(long id) {
        try(Connection c = ds.getConnection()){
            try (PreparedStatement st = c.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                st.setLong(1, id);
                st.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error removing user from database", e);
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
            throw new RuntimeException("Error clearing quiz history", e);
        }
    }
}
