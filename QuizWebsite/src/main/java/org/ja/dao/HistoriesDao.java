package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.History;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
create table history(
    history_id bigint primary key auto_increment,
    user_id bigint not null,
    quiz_id bigint not null,
    score double not null default 0,
    completion_time bigint not null,
    completion_date timestamp default current_timestamp,

    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (user_id) references users(user_id) on delete cascade
);
 */
public class HistoriesDao {
    private final BasicDataSource dataSource;

    public HistoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertHistory(History history){
        String sql = "INSERT INTO history (user_id, quiz_id, score, completion_time, completion_date) VALUES (?,?,?,?,?)";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, history.getUserId());
            ps.setLong(2, history.getQuizId());
            ps.setDouble(3, history.getScore());
            ps.setLong(4, history.getCompletionTime());
            ps.setTimestamp(5, history.getCompletionDate());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next())
                    history.setHistoryId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting history into database", e);
        }
    }

    public void removeHistory(long historyId){
        String sql = "DELETE FROM history WHERE history_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, historyId);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing history from database", e);
        }
    }

    public ArrayList<History> getHistoriesByUserIdSortedByDate(long userId){
        ArrayList<History> histories = new ArrayList<>();

        String sql = "SELECT * FROM history WHERE user_id = ? ORDER BY completion_date DESC";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    histories.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories by user_id from database", e);
        }

        return histories;
    }

    public ArrayList<History> getHistoriesByQuizIdSortedByDate(long quizId){
        ArrayList<History> histories = new ArrayList<>();

        String sql = "SELECT * FROM history WHERE quiz_id = ? ORDER BY completion_date DESC";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    histories.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories by quiz_id from database", e);
        }

        return histories;
    }

    private History retrieveHistory(ResultSet rs) throws SQLException {
        return new History(rs.getLong("history_id"), rs.getLong("user_id"),
                rs.getLong("quiz_id"), rs.getDouble("score"),
                rs.getLong("completion_time"), rs.getTimestamp("completion_date"));
    }
}
