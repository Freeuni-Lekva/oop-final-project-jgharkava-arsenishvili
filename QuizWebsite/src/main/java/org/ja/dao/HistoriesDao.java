package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.History;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private BasicDataSource dataSource;
    public HistoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertHistory(History history){
        String sql="INSERT INTO history (user_id, quiz_id, score, completion_time, completion_date) VALUES (?,?,?,?,?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, history.getUserId());
            ps.setLong(2, history.getQuizId());
            ps.setDouble(3, history.getScore());
            ps.setLong(4, history.getCompletionTime());
            ps.setTimestamp(5, history.getCompletionDate());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()){
                history.setHistoryId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeHistory(long historyId){
        String sql="DELETE FROM history WHERE history_id="+historyId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<History> getHistoriesByUserIdSortedByDate(long userId){
        String sql="SELECT * FROM history WHERE user_id="+userId+" ORDER BY completion_date DESC";
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<History> histories = new ArrayList<>();
            while(rs.next()){
                History history = new History();
                history.setHistoryId(rs.getLong(1));
                history.setUserId(rs.getLong(2));
                history.setQuizId(rs.getLong(3));
                history.setScore(rs.getDouble(4));
                history.setCompletionTime(rs.getLong(5));
                history.setCompletionDate(rs.getTimestamp(6));
                histories.add(history);
            }
            return histories;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<History> getHistoriesByQuizIdSortedByDate(long quizId){
        String sql="SELECT * FROM history WHERE quiz_id="+quizId+" ORDER BY completion_date DESC";
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<History> histories = new ArrayList<>();
            while(rs.next()){
                History history = new History();
                history.setHistoryId(rs.getLong(1));
                history.setUserId(rs.getLong(2));
                history.setQuizId(rs.getLong(3));
                history.setScore(rs.getDouble(4));
                history.setCompletionTime(rs.getLong(5));
                history.setCompletionDate(rs.getTimestamp(6));
                histories.add(history);
            }
            return histories;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
