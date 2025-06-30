package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.History;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoriesDao {
    private final BasicDataSource dataSource;
    private long cnt = 0;
    public HistoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// updates quiz's participant count in quizzes table
    public void insertHistory(History history) throws SQLException{
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
                    long historyId = rs.getLong(1);
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

        updateQuizParticipantCount(history.getQuizId());
    }

    public void removeHistory(long historyId){
        String sql = "DELETE FROM history WHERE history_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, historyId);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing history from database", e);
        }
    }

    /// retrieves user's history, sorted by decreasing completion date
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

    /// retrieves quiz's history, sorted by decreasing completion date
    /// todo: should add limit here
    public ArrayList<History> getHistoriesByQuizIdSortedByDate(long quizId){
        ArrayList<History> histories = new ArrayList<>();

        String sql = "SELECT DISTINCT * FROM history WHERE quiz_id = ? ORDER BY completion_date DESC";

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

    public ArrayList<History> getTopNDistinctHistoriesByQuizId(long quizId, int limit) {
        ArrayList<History> histories = new ArrayList<>();

        String sql = """
            SELECT ranked.*
            FROM (
                SELECT *,
                       ROW_NUMBER() OVER (
                           PARTITION BY user_id
                           ORDER BY score DESC, completion_time ASC, completion_date DESC
                       ) AS rn
                FROM history
                WHERE quiz_id = ?
            ) ranked
            WHERE rn = 1
            ORDER BY score DESC, completion_time ASC, completion_date DESC
            LIMIT ?
        """;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, quizId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    histories.add(retrieveHistory(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving top N distinct histories", e);
        }

        return histories;
    }

    public ArrayList<History> getDistinctTopHistoriesByQuizId(long quizId) {
        ArrayList<History> histories = new ArrayList<>();

        String sql = """
        SELECT ranked.*
        FROM (
            SELECT *,
                   ROW_NUMBER() OVER (
                       PARTITION BY user_id
                       ORDER BY score DESC, completion_time ASC, completion_date DESC
                   ) AS rn
            FROM history
            WHERE quiz_id = ?
        ) ranked
        WHERE rn = 1
        ORDER BY score DESC, completion_time ASC, completion_date DESC
    """;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    histories.add(retrieveHistory(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving distinct top histories", e);
        }

        return histories;
    }

    public ArrayList<History> getTopPerformersByQuizIdAndRange(long quizId, String range, int limit) {
        ArrayList<History> histories = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE quiz_id = ? AND completion_date >= ? ORDER BY score DESC, completion_time ASC, completion_date DESC LIMIT ?";

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = switch (range) {
            case "last_week" -> now.minusWeeks(1);
            case "last_month" -> now.minusMonths(1);
            case "last_year" -> now.minusYears(1);
            default -> now.minusDays(1);
        };

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, quizId);
            ps.setTimestamp(2, Timestamp.valueOf(cutoff));
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    histories.add(retrieveHistory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving top performers filtered by range", e);
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

    public ArrayList<History> getUserFriendsHistorySortedByCompletionDate(long userId){
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
                "ORDER BY h.completion_date DESC";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);

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
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }

    public boolean contains(long hid){
        String sql = "SELECT COUNT(*) FROM history WHERE history_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, hid);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
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

    private void updateQuizParticipantCount(long quizId) throws SQLException {
        String selectSQl = "SELECT COUNT(DISTINCT  user_id) AS participantCount FROM history WHERE quiz_id = ?";
        String updateSQL = "UPDATE quizzes SET participant_count = ? WHERE quiz_id = ?";

        long participantCount = -1;

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(selectSQl)){

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    participantCount = rs.getLong("participantCount");

            }
        } catch (SQLException e){
            throw new RuntimeException("Error querying number of participants from quiz database", e);
        }
        if(participantCount == -1) {
            return;
        }
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(updateSQL)){

            ps.setLong(1, participantCount);
            ps.setLong(2, quizId);

            ps.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Error inserting number of participants into database", e);
        }
    }
}
