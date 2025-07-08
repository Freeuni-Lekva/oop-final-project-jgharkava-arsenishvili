package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ja.model.OtherObjects.History;
import org.ja.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoriesDao {
    private static final Log log = LogFactory.getLog(HistoriesDao.class);
    private final BasicDataSource dataSource;
    private long cnt = 0;
    public HistoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// updates quiz's participant count in quizzes table
    public void insertHistory(History history) throws SQLException{
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaa");
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

        System.out.println("aaaaaaaaaaaa");
        checkAchievements(history);
        updateQuizParticipantCount(history.getQuizId());
        System.out.println("modixar aq??");
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

    public ArrayList<History> getTopPerformersByQuizIdAndRange(long quizId, String range) {
        ArrayList<History> histories = new ArrayList<>();
        String sql = "SELECT * FROM history WHERE quiz_id = ? AND completion_date >= ? ORDER BY score DESC, completion_time ASC, completion_date DESC";

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

    public long getTotalAttempts(long quizId){
        String sql = "SELECT COUNT(*) FROM history WHERE quiz_id = ?";

        return (long) statisticsCalculations(quizId, sql);
    }

    public double getAverageScore(long quizId){
        String sql = "SELECT AVG(score) FROM history WHERE quiz_id = ?";

        return statisticsCalculations(quizId, sql);
    }

    public long getMaximumScore(long quizId){
        String sql = "SELECT MAX(score) FROM history WHERE quiz_id = ?";

        return (long) statisticsCalculations(quizId, sql);
    }

    public long getMinimumScore(long quizId){
        String sql = "SELECT MIN(score) FROM history WHERE quiz_id = ?";

        return (long) statisticsCalculations(quizId, sql);
    }

    public double getAverageTime(long quizId){
        String sql = "SELECT AVG(completion_time) FROM history WHERE quiz_id = ?";

        return statisticsCalculations(quizId, sql);
    }

    private double statisticsCalculations(long quizId, String sql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error calculating total attempts on this quiz", e);
        }

        return 0.0;
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

    private void checkAchievements(History history) throws SQLException{
        long userId = history.getUserId();

        // 1. First step
        if (!hasAchievement(userId, Constants.AchievementIds.FIRST_STEP)) {
            System.out.println("xxxx");
            grantAchievement(userId, Constants.AchievementIds.FIRST_STEP);
        }

        // 2. Quiz Addict (10 completed quizzes)
        if (!hasAchievement(userId, Constants.AchievementIds.QUIZ_ADDICT) && getCompletedQuizCount(userId) >= 10) {
            System.out.println("eee");
            grantAchievement(userId, Constants.AchievementIds.QUIZ_ADDICT);
        }

        // 3. Flawless Victory
        if (!hasAchievement(userId, Constants.AchievementIds.FLAWLESS_VICTORY) && getPerfectScoreCount(userId) > 0) {
            System.out.println("ccc");
            grantAchievement(userId, Constants.AchievementIds.FLAWLESS_VICTORY);
        }

        // 4. Quiz Master (5 perfect scores)
        if (!hasAchievement(userId, Constants.AchievementIds.QUIZ_MASTER) && getPerfectScoreCount(userId) >= 5) {
            System.out.println("bbb");
            grantAchievement(userId, Constants.AchievementIds.QUIZ_MASTER);
        }

        // 5. Speed Demon
        if (!hasAchievement(userId, Constants.AchievementIds.SPEED_DEMON) && history.getCompletionTime() <= 1) {
            System.out.println("aaaa");
            grantAchievement(userId, Constants.AchievementIds.SPEED_DEMON);
        }

        System.out.println(hasAchievement(userId, Constants.AchievementIds.SPEED_DEMON));
        System.out.println( hasAchievement(userId, Constants.AchievementIds.QUIZ_MASTER));
        System.out.println(hasAchievement(userId, Constants.AchievementIds.FLAWLESS_VICTORY));
        System.out.println(hasAchievement(userId, Constants.AchievementIds.QUIZ_ADDICT));
        System.out.println(hasAchievement(userId, Constants.AchievementIds.FIRST_STEP));
    }

    private void grantAchievement(long userId, long achievementId) throws SQLException{
        String sql = "INSERT INTO user_achievement (user_id, achievement_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, userId);
            ps.setLong(2, achievementId);

            ps.executeUpdate();
        }

    }

    private boolean hasAchievement(long userId, long achievementId) throws SQLException {
        String sql = "SELECT 1 FROM user_achievement WHERE user_id = ? AND achievement_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, achievementId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int getCompletedQuizCount(long userId) throws SQLException{
        String sql = "SELECT COUNT(DISTINCT quiz_id) FROM history WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                if(rs.next())
                    return rs.getInt(1);
            }
        }

        return -1;
    }

    private int getPerfectScoreCount(long userId) throws SQLException{
        String sql = "SELECT COUNT(DISTINCT q.quiz_id) " +
                     "FROM history h JOIN quizzes q on h.quiz_id = q.quiz_id " +
                     "WHERE h.score = q.quiz_score AND h.user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next())
                    return rs.getInt(1);
            }
        }

        return -1;
    }
}

