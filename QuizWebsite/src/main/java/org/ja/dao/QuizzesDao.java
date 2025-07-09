package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.quiz.Quiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) class for managing quiz-related operations in the database.
 */
public class QuizzesDao {
    private final BasicDataSource dataSource;


    /**
     * Constructs a QuizzesDao using the provided data source.
     *
     * @param dataSource the data source to use for database connections
     */
    public QuizzesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new quiz into the database.
     *
     * @param quiz the quiz to insert
     * @return true if the quiz was inserted successfully; false otherwise
     */
    public boolean insertQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes ( quiz_name, quiz_description, quiz_score, average_rating, " +
                "participant_count, time_limit_in_minutes, category_id," +
                "creator_id, question_order_status, question_placement_status," +
                "question_correction_status ) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, quiz.getName());
            ps.setString(2, quiz.getDescription());
            ps.setInt(3, quiz.getScore());
            ps.setDouble(4, quiz.getAvgRating());
            ps.setLong(5, quiz.getParticipantCount());
            ps.setInt(6, quiz.getTimeInMinutes());
            ps.setLong(7, quiz.getCategoryId());
            ps.setLong(8, quiz.getCreatorId());
            ps.setString(9,quiz.getQuestionOrder());
            ps.setString(10, quiz.getQuestionPlacement());
            ps.setString(11, quiz.getQuestionCorrection());

            if (ps.executeUpdate() == 0){
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) {
                    quiz.setId(rs.getLong(1));
                    setQuizCreationDate(quiz, c);
                } else {
                    throw new RuntimeException("Quiz insertion succeeded, but no generated quiz ID was returned.");
                }
            }

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz into database", e);
        }
    }


    /**
     * Updates the title of a quiz.
     *
     * @param id    the ID of the quiz to update
     * @param title the new title to set
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizTitle(long id, String title){
        String sql = "UPDATE quizzes SET quiz_name = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, title);
            preparedStatement.setLong(2, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz title by id", e);
        }
    }


    /**
     * Updates the description of a quiz.
     *
     * @param id          the ID of the quiz to update
     * @param description the new description to set
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizDescription(long id, String description){
        String sql = "UPDATE quizzes SET quiz_description = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, description);
            preparedStatement.setLong(2, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz description by id", e);
        }
    }


    /**
     * Updates the time limit (in minutes) for a quiz.
     *
     * @param id        the ID of the quiz to update
     * @param timeLimit the new time limit in minutes
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizTimeLimit(long id, int timeLimit){
        String sql = "UPDATE quizzes SET time_limit_in_minutes = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, timeLimit);
            preparedStatement.setLong(2, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz time limit by id", e);
        }
    }


    /**
     * Updates the category of a quiz.
     *
     * @param quizId     the ID of the quiz to update
     * @param categoryId the new category ID to set
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizCategory(long quizId, long categoryId){
        String sql = "UPDATE quizzes SET category_id = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setLong(1, categoryId);
            preparedStatement.setLong(2, quizId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz category by id", e);
        }
    }


    /**
     * Updates the question order status for a quiz.
     *
     * @param quizId              the ID of the quiz
     * @param questionOrderStatus the new question order status
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizQuestionOrderStatus(long quizId, String questionOrderStatus){
        String sql = "UPDATE quizzes SET question_order_status = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, questionOrderStatus);
            preparedStatement.setLong(2, quizId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz question order status by id", e);
        }
    }


    /**
     * Updates the question placement status for a quiz.
     *
     * @param quizId                 the ID of the quiz
     * @param questionPlacementStatus the new placement status
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizQuestionPlacementStatus(long quizId, String questionPlacementStatus){
        String sql = "UPDATE quizzes SET question_placement_status = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, questionPlacementStatus);
            preparedStatement.setLong(2, quizId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz question placement status by id", e);
        }
    }


    /**
     * Updates the question correction status for a quiz.
     *
     * @param quizId                  the ID of the quiz
     * @param questionCorrectionStatus the new correction status
     * @return true if the update was successful; false otherwise
     */
    public boolean updateQuizQuestionCorrectionStatus(long quizId, String questionCorrectionStatus){
        String sql = "UPDATE quizzes SET question_correction_status = ? WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, questionCorrectionStatus);
            preparedStatement.setLong(2, quizId);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating quiz question correction status by id", e);
        }
    }


    /**
     * Deletes a quiz by its ID.
     *
     * @param id the ID of the quiz to delete
     * @return true if the quiz was successfully deleted; false otherwise
     */
    public boolean removeQuizById(long id) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setLong(1, id);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz by id from database", e);
        }
    }


    /**
     * Retrieves quizzes from the database based on the given filter.
     *
     * @param filter the filter criteria for retrieving quizzes
     * @return a list of quizzes matching the filter
     */
    public List<Quiz> filterQuizzes(Filter filter) {
        String sql = "SELECT DISTINCT quizzes.quiz_id, quiz_name, quiz_description, average_rating, " +
                "participant_count, creation_date, time_limit_in_minutes, quizzes.category_id, " +
                "creator_id, question_order_status, question_placement_status, question_correction_status, quiz_score " +
                "FROM quizzes left join categories on categories.category_id = quizzes.category_id " +
                "left join quiz_tag on quizzes.quiz_id = quiz_tag.quiz_id " +
                "left join tags on tags.tag_id = quiz_tag.tag_id " +
                "WHERE " + filter.buildWhereClause() + " ORDER BY " + filter.buildOrderByClause();

        List<Quiz> quizzes = new ArrayList<>();

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            List<Object> parameters = filter.getParameters();

            for (int i = 0; i < parameters.size(); i++){
                ps.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    quizzes.add(retrieveQuiz(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error filtering quizzes from database", e);
        }

        return quizzes;
    }


    /**
     * Retrieves all quizzes from the database sorted by creation date (most recent first).
     *
     * @return a list of quizzes sorted by creation date
     */
    public List<Quiz> getQuizzesSortedByCreationDate() {
        String sql = "SELECT * FROM quizzes ORDER BY creation_date DESC";
        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next())
                quizzes.add(retrieveQuiz(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quizzes by creation date", e);
        }

        return quizzes;
    }


    /**
     * Retrieves quizzes created by a user's friends, sorted by creation date.
     *
     * @param userId the ID of the user whose friends' quizzes to retrieve
     * @return a list of quizzes created by friends
     */
    public List<Quiz> getFriendsQuizzesSortedByCreationDate(long userId) {
        String sql = "SELECT q.*" +
                "        FROM quizzes q" +
                "        JOIN (" +
                "            SELECT" +
                "                CASE" +
                "                    WHEN first_user_id = ? THEN second_user_id" +
                "                    ELSE first_user_id" +
                "                END AS friend_id" +
                "            FROM friendships" +
                "            WHERE (first_user_id = ? OR second_user_id = ?)" +
                "              AND friendship_status = 'friends'" +
                "        ) f ON q.creator_id = f.friend_id" +
                "        ORDER BY q.creation_date DESC";
        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
             stmt.setLong(1, userId);
             stmt.setLong(2, userId);
             stmt.setLong(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    quizzes.add(retrieveQuiz(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quizzes by creation date", e);
        }

        return quizzes;
    }


    /**
     * Retrieves quizzes from the database sorted by participant count in descending order.
     *
     * @return a list of popular quizzes
     */
    public List<Quiz> getQuizzesSortedByParticipantCount() {
        String sql = "SELECT * FROM quizzes ORDER BY participant_count DESC";
        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

             while (rs.next())
                quizzes.add(retrieveQuiz(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quizzes by participant quiz database", e);
        }

        return quizzes;
    }


    /**
     * Retrieves a quiz by its ID.
     *
     * @param id the ID of the quiz
     * @return the Quiz object if found; null otherwise
     */
    public Quiz getQuizById(long id) {
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveQuiz(rs);
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving quiz by ID", e);
        }
    }


    /**
     * Retrieves a quiz by its name.
     *
     * @param name the name of the quiz
     * @return the Quiz object if found; null otherwise
     */
    public Quiz getQuizByName(String name) {
        String sql = "SELECT * FROM quizzes WHERE quiz_name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveQuiz(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving quiz by name", e);
        }
    }


    /**
     * Retrieves all quizzes created by a specific user.
     *
     * @param id the creator's user ID
     * @return a list of quizzes created by the user
     */
    public List<Quiz> getQuizzesByCreatorId(long id) {
        String sql = "SELECT * FROM quizzes WHERE creator_id = ?";
        List<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
             stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(retrieveQuiz(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying quizzes by creator id", e);
        }
        return quizzes;
    }



    // --- Helper Methods ---


    /**
     * Loads the creation date from the database for the given quiz ID
     * and sets it on the provided Quiz object.
     *
     * @param quiz   the Quiz object to update
     * @param connection the database connection to use
     * @throws SQLException if a database error occurs
     */
    private void setQuizCreationDate(Quiz quiz, Connection connection) throws SQLException {
        String sql = "SELECT creation_date FROM quizzes WHERE quiz_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, quiz.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    quiz.setCreationDate(rs.getTimestamp("creation_date"));
                } else {
                    throw new RuntimeException("Quiz not found in database after insertion: ID = " + quiz.getId());
                }
            }
        }
    }


    /**
     * Helper method that builds a Quiz object from a ResultSet row.
     *
     * @param rs the ResultSet containing quiz data
     * @return the constructed Quiz object
     * @throws SQLException if a database access error occurs
     */
    private Quiz retrieveQuiz(ResultSet rs) throws SQLException {
        return new Quiz(rs.getLong("quiz_id"), rs.getString("quiz_name"),
                rs.getString("quiz_description"), rs.getInt("quiz_score"),
                rs.getDouble("average_rating"),
                rs.getLong("participant_count"), rs.getTimestamp("creation_date"),
                rs.getInt("time_limit_in_minutes"), rs.getLong("category_id"),
                rs.getLong("creator_id"), rs.getString("question_order_status"),
                rs.getString("question_placement_status"), rs.getString("question_correction_status"));
    }

}
