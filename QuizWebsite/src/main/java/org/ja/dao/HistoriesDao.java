package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ja.model.OtherObjects.History;
import org.ja.utils.Constants;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Data Access Object for managing quiz histories, participant stats, and related achievements.
 */
public class HistoriesDao {
    private static final Log log = LogFactory.getLog(HistoriesDao.class);

    private final BasicDataSource dataSource;


    /**
     * Constructs a HistoriesDao using the given data source.
     * @param dataSource the DB connection pool to use
     */
    public HistoriesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a quiz history record and updates related quiz stats and achievements.
     * Also sets the generated history ID and completion date on the provided object.
     *
     * @param history the History object to insert and enrich
     * @throws RuntimeException if database interaction fails
     */
    public boolean insertHistory(History history) throws SQLException{
        String sql = "INSERT INTO history (user_id, quiz_id, score, completion_time) VALUES (?,?,?,?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, history.getUserId());
            ps.setLong(2, history.getQuizId());
            ps.setInt(3, history.getScore());
            ps.setDouble(4, history.getCompletionTime());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    history.setHistoryId(rs.getLong(1));

                    setCompletionDate(history, c);
                } else {
                    throw new RuntimeException("History inserted but no generated key returned.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting history into database", e);
        }

        try {
            checkAchievements(history);
            updateQuizParticipantCount(history.getQuizId());
        } catch (SQLException e) {
            throw new RuntimeException("Error updating achievements or participant count", e);
        }

        return true;
    }


    /**
     * Retrieves recent history records for a specific user, sorted by completion date descending.
     *
     * @param userId the user ID to query by
     * @param limit the maximum number of records to retrieve
     * @return a list of History objects; empty if none found
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getHistoriesByUserId(long userId, int limit){
        List<History> histories = new ArrayList<>();

        String sql = "SELECT * FROM history WHERE user_id = ? ORDER BY completion_date DESC LIMIT ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    histories.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories by user_id from database", e);
        }

        return histories;
    }


    /**
     * Retrieves recent history records for a specific quiz, sorted by completion date descending.
     *
     * @param quizId the quiz ID to query by
     * @param limit the maximum number of records to retrieve
     * @return a list of History objects; empty if none found
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getHistoriesByQuizId(long quizId, int limit){
        List<History> histories = new ArrayList<>();

        String sql = "SELECT DISTINCT * FROM history WHERE quiz_id = ? ORDER BY completion_date DESC LIMIT ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    histories.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories by quiz_id from database", e);
        }

        return histories;
    }


    /**
     * Retrieves top distinct user performances for a quiz, limited by the specified count.
     * Each user appears only once with their best score.
     *
     * @param quizId the quiz ID to query by
     * @param limit the maximum number of top performances to retrieve
     * @return a list of History objects representing the best attempt per user
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getTopNDistinctHistoriesByQuizId(long quizId, int limit) {
        List<History> histories = new ArrayList<>();

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


    /**
     * Retrieves all distinct top user performances for a quiz.
     * Each user appears only once with their best score, limited by the specified count.
     *
     * @param quizId the quiz ID to query by
     * @param limit the maximum number of top performances to retrieve
     * @return a list of History objects representing the best attempt per user
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getDistinctTopHistoriesByQuizId(long quizId, int limit) {
        List<History> histories = new ArrayList<>();

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
            throw new RuntimeException("Error retrieving distinct top histories", e);
        }

        return histories;
    }


    /**
     * Retrieves top quiz performances within a specified time range, limited by the given count.
     *
     * @param quizId the quiz ID to query by
     * @param range the time range filter ("last_day", "last_week", "last_month", or "last_year")
     * @param limit the maximum number of records to retrieve
     * @return a list of top History objects within the specified range
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getTopPerformersByQuizIdAndRange(long quizId, String range, int limit) {
        List<History> histories = new ArrayList<>();
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


    /**
     * Retrieves a user's history for a specific quiz, limited by the specified count.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @param limit the maximum number of records to retrieve
     * @return a list of History objects for the user and quiz; empty if none found
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getUserHistoryByQuiz(long userId, long quizId, int limit){
        List<History> historyList = new ArrayList<>();

        String sql = "SELECT * FROM history WHERE user_id = ? AND quiz_id = ? LIMIT ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, quizId);
            ps.setInt(3, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    historyList.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories by quiz_id and user_id from database", e);
        }

        return historyList;
    }


    /**
     * Retrieves history records of a user's friends for a specific quiz, sorted by completion date descending.
     *
     * @param userId the user ID
     * @param quizId the quiz ID
     * @param limit the maximum number of records to retrieve
     * @return a list of History objects of friends' attempts on the quiz
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getUserFriendsHistoryByQuiz(long userId, long quizId, int limit){
        List<History> historyList = new ArrayList<>();

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
                "WHERE h.quiz_id = ? ORDER BY h.completion_date DESC LIMIT ?;";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setLong(4, quizId);
            ps.setInt(5, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    historyList.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories for friends by quiz_id and user_id from database", e);
        }

        return historyList;
    }


    /**
     * Retrieves recent history records of a user's friends across all quizzes, sorted by completion date descending.
     *
     * @param userId the user ID
     * @param limit the maximum number of records to retrieve
     * @return a list of History objects of friends' quiz attempts
     * @throws RuntimeException if a database error occurs
     */
    public List<History> getUserFriendsHistory(long userId, int limit){
        List<History> historyList = new ArrayList<>();

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
                "ORDER BY h.completion_date DESC LIMIT ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.setLong(3, userId);
            ps.setInt(4, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    historyList.add(retrieveHistory(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving histories for friends by quiz_id and user_id from database", e);
        }

        return historyList;
    }


    /**
     * Returns the total number of attempts made on a quiz.
     *
     * @param quizId the quiz ID
     * @return count of attempts, or 0 if none
     * @throws RuntimeException if a database error occurs
     */
    public long getTotalAttempts(long quizId){
        String sql = "SELECT COUNT(*) FROM history WHERE quiz_id = ?";

        return (long) statisticsCalculations(quizId, sql);
    }


    /**
     * Returns the average score for a quiz.
     *
     * @param quizId the quiz ID
     * @return average score or 0.0 if no attempts
     * @throws RuntimeException if a database error occurs
     */
    public double getAverageScore(long quizId){
        String sql = "SELECT AVG(score) FROM history WHERE quiz_id = ?";

        return statisticsCalculations(quizId, sql);
    }


    /**
     * Returns the maximum score achieved on a quiz.
     *
     * @param quizId the quiz ID
     * @return maximum score or 0 if no attempts
     * @throws RuntimeException if a database error occurs
     */
    public long getMaximumScore(long quizId){
        String sql = "SELECT MAX(score) FROM history WHERE quiz_id = ?";

        return (long) statisticsCalculations(quizId, sql);
    }


    /**
     * Returns the minimum score achieved on a quiz.
     *
     * @param quizId the quiz ID
     * @return minimum score or 0 if no attempts
     * @throws RuntimeException if a database error occurs
     */
    public long getMinimumScore(long quizId){
        String sql = "SELECT MIN(score) FROM history WHERE quiz_id = ?";

        return (long) statisticsCalculations(quizId, sql);
    }


    /**
     * Returns the average completion time for a quiz.
     *
     * @param quizId the quiz ID
     * @return average completion time or 0.0 if no attempts
     * @throws RuntimeException if a database error occurs
     */
    public double getAverageTime(long quizId){
        String sql = "SELECT AVG(completion_time) FROM history WHERE quiz_id = ?";

        return statisticsCalculations(quizId, sql);
    }


    // --- Helper Methods ---

    /**
     * Helper method to update the participant count field in the quizzes table for the specified quiz.
     *
     * @param quizId the quiz ID
     * @throws RuntimeException if a database error occurs
     */
    private void updateQuizParticipantCount(long quizId){
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

        if (participantCount == -1) {
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


    /**
     * Helper method to check and grant achievements to the user based on their history.
     *
     * @param history the user's history record used to evaluate achievements
     * @throws SQLException if a database error occurs
     */
    private void checkAchievements(History history) throws SQLException{
        long userId = history.getUserId();

        // 1. First step
        if (!hasAchievement(userId, Constants.AchievementIds.FIRST_STEP)) {
            grantAchievement(userId, Constants.AchievementIds.FIRST_STEP);
        }

        // 2. Quiz Addict (10 completed quizzes)
        if (!hasAchievement(userId, Constants.AchievementIds.QUIZ_ADDICT) && getCompletedQuizCount(userId) >= 10) {
            grantAchievement(userId, Constants.AchievementIds.QUIZ_ADDICT);
        }

        // 3. Flawless Victory
        if (!hasAchievement(userId, Constants.AchievementIds.FLAWLESS_VICTORY) && getPerfectScoreCount(userId) > 0) {
            grantAchievement(userId, Constants.AchievementIds.FLAWLESS_VICTORY);
        }

        // 4. Quiz Master (5 perfect scores)
        if (!hasAchievement(userId, Constants.AchievementIds.QUIZ_MASTER) && getPerfectScoreCount(userId) >= 5) {
            grantAchievement(userId, Constants.AchievementIds.QUIZ_MASTER);
        }

        // 5. Speed Demon
        if (!hasAchievement(userId, Constants.AchievementIds.SPEED_DEMON) && history.getCompletionTime() <= 1) {
            grantAchievement(userId, Constants.AchievementIds.SPEED_DEMON);
        }
    }


    /**
     * Helper to grant an achievement to a user.
     *
     * @param userId the user ID
     * @param achievementId the achievement ID to grant
     * @return true if the achievement was successfully granted, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean grantAchievement(long userId, long achievementId) throws SQLException{
        String sql = "INSERT INTO user_achievement (user_id, achievement_id) VALUES (?, ?)";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, userId);
            ps.setLong(2, achievementId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error granting an achievement to a user", e);
        }

    }

    /**
     * Helper to check if a user already has a specific achievement.
     *
     * @param userId the user ID
     * @param achievementId the achievement ID to check
     * @return true if the user has the achievement, false otherwise
     * @throws SQLException if a database error occurs
     */
    private boolean hasAchievement(long userId, long achievementId) throws SQLException {
        String sql = "SELECT 1 FROM user_achievement WHERE user_id = ? AND achievement_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, achievementId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e){
            throw new RuntimeException("Error checking whether a user has an achievement", e);
        }
    }


    /**
     * Helper to count how many distinct quizzes the user has completed.
     *
     * @param userId the user ID
     * @return number of distinct quizzes completed, or -1 if error
     * @throws SQLException if a database error occurs
     */
    private int getCompletedQuizCount(long userId) throws SQLException{
        String sql = "SELECT COUNT(DISTINCT quiz_id) FROM history WHERE user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return rs.getInt(1);
            }
        } catch (SQLException e){
            throw new RuntimeException("Error counting distinct quizzes the user has completed", e);
        }

        return -1;
    }


    /**
     * Helper to count how many quizzes the user has scored perfectly on.
     *
     * @param userId the user ID
     * @return number of perfect scores, or -1 if error
     * @throws SQLException if a database error occurs
     */
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
        } catch (SQLException e){
            throw new RuntimeException("Error counting how many quizzes the user has score perfectly on", e);
        }

        return -1;
    }


    /**
     * Helper method to sets the completion date on the provided History object by querying the database.
     *
     * @param history the history object to update
     * @param connection the open DB connection to use
     * @throws SQLException if database interaction fails
     */
    private void setCompletionDate(History history, Connection connection) throws SQLException {
        String dateSql = "SELECT completion_date FROM history WHERE history_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(dateSql)) {
            ps.setLong(1, history.getHistoryId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    history.setCompletionDate(rs.getTimestamp("completion_date"));
                } else {
                    throw new RuntimeException("Completion date not found for history ID: " + history.getHistoryId());
                }
            }
        }
    }

    /**
     * Helper method for single-value statistical calculations (COUNT, AVG, MAX, MIN).
     *
     * @param quizId the quiz ID
     * @param sql the SQL query with a single result column
     * @return double value of the calculation or 0 if none
     * @throws RuntimeException if a database error occurs
     */
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


    /**
     * Constructs a History object from the current row of the ResultSet.
     *
     * @param rs the ResultSet positioned at the row
     * @return History object populated from the ResultSet
     * @throws SQLException if an error occurs reading from ResultSet
     */
    private History retrieveHistory(ResultSet rs) throws SQLException {
        return new History(rs.getLong("history_id"), rs.getLong("user_id"),
                rs.getLong("quiz_id"), rs.getInt("score"),
                rs.getDouble("completion_time"), rs.getTimestamp("completion_date"));
    }
}

