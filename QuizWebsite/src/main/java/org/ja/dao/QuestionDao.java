package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.quiz.question.*;
import org.ja.utils.Constants;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing questions in the database.
 */
public class QuestionDao extends BaseDao{

    /**
     * Constructs a QuestionDao with the given data source.
     *
     * @param dataSource the database connection pool
     */
    public QuestionDao(BasicDataSource dataSource) {
        super(dataSource);
    }


    /**
     * Inserts a new question into the database.
     *
     * @param question the question to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertQuestion(Question question) {
        String sql = "INSERT INTO questions (quiz_id, question, image_url, " +
                "question_type, num_answers, order_status) VALUES (?,?, ?, ?, ?, ?);";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getImageUrl());
            ps.setString(4, question.getQuestionType());
            ps.setInt(5, question.getNumAnswers());
            ps.setString(6, question.getOrderStatus());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0){
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    question.setQuestionId(rs.getLong(1));
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }
            }

            updateQuizScore(question.getQuizId());

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Questions into database", e);
        }
    }


    /**
     * Removes a question from the database and updates the quiz score.
     *
     * @param questionId the ID of the question to remove
     * @return true if the question was successfully deleted, false otherwise
     */
    public boolean removeQuestion(long questionId) {
        Question question = getQuestionById(questionId);

        if (question == null) return false;

        String sql = "DELETE FROM questions WHERE question_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, questionId);

            if (ps.executeUpdate() == 0){
                return false;
            }

            updateQuizScore(question.getQuizId());

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing question from database", e);
        }
    }



    /**
     * Retrieves all questions for a given quiz.
     *
     * @param quizId the ID of the quiz
     * @return a list of questions belonging to the quiz
     */
    public List<Question> getQuizQuestions(long quizId) {
        List<Question> questions = new ArrayList<>();

        String sql = "SELECT * FROM questions WHERE quiz_id = ? ORDER BY question_id";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    questions.add(retrieveQuestion(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz questions from database", e);
        }

        return questions;
    }


    /**
     * Updates the question text for a specific question.
     *
     * @param questionId   the ID of the question to update
     * @param questionText the new text for the question
     * @return true if the update was successful, false otherwise
     */
    public boolean updateQuestionText(long questionId, String questionText){
        return updateQuestionField("question", questionText, questionId);
    }

    /**
     * Updates the image URL of a question.
     *
     * @param questionId the ID of the question to update
     * @param imageUrl   the new image URL
     * @return true if the update was successful, false otherwise
     */
    public boolean updateQuestionImage(long questionId, String imageUrl){
        return updateQuestionField("image_url", imageUrl, questionId);
    }



    // --- Helper Methods ---


    /**
     * Updates a specified field of a question in the database.
     *
     * @param fieldName   the column name to update (e.g., "question" or "image_url")
     * @param fieldValue  the new value to set
     * @param questionId  the ID of the question to update
     * @return true if update was successful, false otherwise
     */
    private boolean updateQuestionField(String fieldName, String fieldValue, long questionId) {
        String sql = "UPDATE questions SET " + fieldName + " = ? WHERE question_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, fieldValue);
            ps.setLong(2, questionId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating question field: " + fieldName, e);
        }
    }
}
