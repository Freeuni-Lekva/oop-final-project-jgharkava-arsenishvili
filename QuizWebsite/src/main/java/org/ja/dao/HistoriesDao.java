package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.History;
import org.ja.model.OtherObjects.Message;

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
    private long cnt=0;
    public HistoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertHistory(History history){
        if(contains(history)){
            return;
        }
        String sql = "INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (?,?,?,?)";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, history.getUserId());
            ps.setLong(2, history.getQuizId());
            ps.setLong(3, history.getScore());
            ps.setDouble(4, history.getCompletionTime());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    cnt++;
                    long historyId = rs.getLong("history_id");
                    history.setHistoryId(historyId);

                    String s = "SELECT completion_date FROM history where history_id = ?";

                    try (PreparedStatement preparedStatement = c.prepareStatement(s)){
                        preparedStatement.setLong(1, historyId);

                        try (ResultSet r = preparedStatement.executeQuery()) {
                            if (r.next())
                                history.setCompletionDate(r.getTimestamp("completion_date"));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting history into database", e);
        }
    }

    public void removeHistory(long historyId){
        if(!contains(historyId)){
            return;
        }
        String sql = "DELETE FROM history WHERE history_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, historyId);

            ps.executeUpdate();
            cnt--;
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

    public ArrayList<History> getUserHistoryByQuiz(long userId, long quizId){
        ArrayList<History> historyList = new ArrayList<>();

        String sql = "SELECT * FROM history WHERE user_id = ? AND quiz_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    historyList.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories by quiz_id and user_id from database", e);
        }

        return historyList;
    }

    public ArrayList<History> getUserFriendsHistoryByQuiz(long userId, long quizId){
        ArrayList<History> historyList = new ArrayList<>();

        String sql = "SELECT h.* " +
                "FROM history h " +
                "JOIN (" +
                "    SELECT " +
                "        CASE " +
                "            WHEN first_user_id = ? THEN second_user_id " +
                "            ELSE first_user_id " +
                "        END AS friend_id " +
                "    FROM friendships " +
                "    WHERE (first_user_id = ? OR second_user_id = ?) " +
                "      AND friendship_status = 'friends' " +
                ") f ON h.user_id = f.friend_id " +
                "WHERE h.quiz_id = ?;";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    historyList.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories for friends by quiz_id and user_id from database", e);
        }

        return historyList;
    }
    public boolean contains(History h){
        if(h==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM history WHERE user_id=?" +
                "AND quiz_id=? AND score=? AND completion_time=? AND completion_date=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, h.getUserId());
            ps.setLong(2, h.getQuizId());
            ps.setDouble(3, h.getScore());
            ps.setDouble(4, h.getCompletionTime());
            ps.setTimestamp(5, h.getCompletionDate());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public boolean contains(long hid){
        if(hid<0||hid>cnt){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM history WHERE history_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, hid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public long getCount(){
        return cnt;
    }
    private History retrieveHistory(ResultSet rs) throws SQLException {
        return new History(rs.getLong("history_id"), rs.getLong("user_id"),
                rs.getLong("quiz_id"), rs.getLong("score"),
                rs.getDouble("completion_time"), rs.getTimestamp("completion_date"));
    }
}
