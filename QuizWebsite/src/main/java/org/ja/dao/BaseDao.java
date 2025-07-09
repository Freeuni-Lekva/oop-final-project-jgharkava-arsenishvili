package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.quiz.question.*;
import org.ja.utils.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Abstract base class for all DAO classes, providing shared utility methods
 * to interact with the database for quiz-related operations such as updating
 * quiz scores and question answer counts.
 */
public abstract class BaseDao {
    protected final BasicDataSource dataSource;


    /**
     * Constructs a BaseDao with the given data source.
     *
     * @param dataSource the data source used for obtaining database connections
     */
    protected BaseDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Updates the {@code quiz_score} column of the specified quiz based on the
     * total {@code num_answers} across all its questions.
     *
     * @param quizId the ID of the quiz to update
     * @throws RuntimeException if a database error occurs during update
     */
    protected void updateQuizScore(long quizId) {
        String sql = "UPDATE quizzes " +
                "SET quiz_score = (" +
                "  SELECT COALESCE(SUM(num_answers), 0) " +
                "  FROM questions " +
                "  WHERE quiz_id = ? " +
                ") " +
                "WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, quizId);
            preparedStatement.setLong(2, quizId);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz score by id", e);
        }
    }


    /**
     * Updates the {@code num_answers} of a specific question by summing:
     * <ul>
     *     <li>The number of valid answers in the {@code answers} table</li>
     *     <li>The number of match entries in the {@code matches} table</li>
     * </ul>
     *
     * @param questionId the ID of the question to update
     * @throws RuntimeException if a database error occurs during update
     */
    protected void updateQuestionNumAnswers(long questionId){
        int numAnswers = getQuestionNumAnswers(questionId) + getMatchesNumAnswers(questionId);

        String sql = "UPDATE questions SET num_answers = ? WHERE question_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, numAnswers);
            preparedStatement.setLong(2, questionId);

            preparedStatement.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Error querying num answers for a question", e);
        }
    }


    /**
     * Retrieves the question ID associated with a given answer ID.
     * If no such answer exists, it throws a {@link RuntimeException}.</p>
     *
     * @param answerId the ID of the answer whose question ID is to be retrieved
     * @return the ID of the question to which the answer belongs
     * @throws RuntimeException if the question does not exist or a database error occurs
     */
    protected long getQuestionId(long answerId){
        String sql = "SELECT question_id FROM answers WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, answerId);

            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getLong(1);
                } else {
                    throw new RuntimeException("Question Id not found for this answer");
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer text", e);
        }
    }


    /**
     * Retrieves the quiz ID associated with a given question ID.
     * If no such question exists, it throws a {@link RuntimeException}.</p>
     *
     * @param questionId the ID of the question whose quiz ID is to be retrieved
     * @return the ID of the quiz to which the question belongs
     * @throws RuntimeException if the question does not exist or a database error occurs
     */
    protected long getQuizId(long questionId){
        String sql = "SELECT quiz_id FROM questions WHERE question_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)){

            statement.setLong(1, questionId);

            try (ResultSet resultSet = statement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getLong(1);
                } else {
                    throw new RuntimeException("Quiz Id not found for this question");
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer text", e);
        }
    }


    /**
     * Retrieves a question by its ID.
     *
     * @param questionId the ID of the question
     * @return the corresponding Question object, or null if not found
     */
    protected Question getQuestionById(long questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveQuestion(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching question by ID: " + questionId, e);
        }
    }


    /**
     * Maps a result set row to a corresponding Question subclass based on its type.
     *
     * @param rs the result set containing question data
     * @return a Question object of the appropriate subclass
     * @throws SQLException if a database access error occurs
     */
    protected Question retrieveQuestion(ResultSet rs) throws SQLException {
        String type = rs.getString("question_type");

        switch (type) {
            case Constants.QuestionTypes.RESPONSE_QUESTION -> {
                return new ResponseQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            case Constants.QuestionTypes.PICTURE_RESPONSE_QUESTION -> {
                return new PictureResponseQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            case Constants.QuestionTypes.MATCHING_QUESTION -> {
                return new MatchingQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            case Constants.QuestionTypes.MULTI_ANSWER_QUESTION -> {
                return new MultiAnswerQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            case Constants.QuestionTypes.MULTIPLE_CHOICE_QUESTION -> {
                return new MultipleChoiceQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            case Constants.QuestionTypes.MULTI_CHOICE_MULTI_ANSWER_QUESTION -> {
                return new MultiChoiceMultiAnswersQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            case Constants.QuestionTypes.FILL_IN_THE_BLANK_QUESTION -> {
                return new FillInTheBlankQuestion(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }

            default -> {
                return new Question(rs.getLong("question_id"), rs.getLong("quiz_id"),
                        rs.getString("question"), rs.getString("image_url"),
                        rs.getString("question_type"), rs.getInt("num_answers"),
                        rs.getString("order_status"));
            }
        }

    }



    // --- Helper Methods ---


    /**
     * Helper to return the number of valid (correct) answers for a given question
     * by summing the {@code answer_validity} column in the {@code answers} table.
     *
     * @param questionId the ID of the question
     * @return number of valid answers (0 if none found)
     */
    private int getQuestionNumAnswers(long questionId){
        String sql = "SELECT COALESCE(SUM(answer_validity), 0)" +
                "FROM answers " +
                "WHERE question_id = ?";

        return countAnswers(questionId, sql);
    }


    /**
     * Returns the number of match entries for a given question in the {@code matches} table.
     *
     * @param questionId the ID of the question
     * @return number of match rows (0 if none)
     */
    private int getMatchesNumAnswers(long questionId){
        String sql = "SELECT COUNT(match_id)" +
                "FROM matches " +
                "WHERE question_id = ?";

        return countAnswers(questionId, sql);
    }


    /**
     * Executes a COUNT or SUM query for a given question ID using the provided SQL.
     *
     * @param questionId the question ID to filter the query
     * @param sql        the SQL query string with one parameter placeholder (?)
     * @return the result of the aggregation (0 if no rows found)
     * @throws RuntimeException if a database error occurs
     */
    private int countAnswers(long questionId, String sql) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setLong(1, questionId);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getInt(1);
                } else {
                    return 0;
                }
            }
        } catch (SQLException e){
            throw new RuntimeException("Error querying num answers for a question", e);
        }
    }


}

