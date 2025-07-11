package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.Answer;
import org.ja.model.quiz.question.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data Access Object for managing answers in the quiz system.
 */
public class AnswersDao extends BaseDao{

    /**
     * Constructs a new AnswersDao with the given data source.
     *
     * @param dataSource the data source for database connections
     */
    public AnswersDao(BasicDataSource dataSource) {
        super(dataSource);
    }


    /**
     * Inserts an answer into the database.
     *
     * @param answer the Answer object to insert
     * @return true if the answer was inserted successfully and ID generated; false otherwise
     * @throws RuntimeException if id was not returned or other kind of sql error
     */
    public boolean insertAnswer(Answer answer) {
        String sql = "INSERT INTO answers (question_id, answer_text, answer_order, answer_validity) VALUES (?,?, ?, ?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerText());
            ps.setInt(3, answer.getAnswerOrder());
            ps.setBoolean(4, answer.getAnswerValidity());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) return false;

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    answer.setAnswerId(rs.getLong(1));
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }
            }

            long questionId = answer.getQuestionId();

            updateQuestionNumAnswers(questionId);
            updateQuizScore(getQuizId(questionId));

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting answer into database", e);
        }
    }


    /**
     * Removes an answer from the database by its ID.
     * Does nothing if the answer does not exist.
     *
     * @param answerId the ID of the answer to remove
     * @throws RuntimeException if a database error occurs
     */
    public boolean removeAnswer(long answerId) {

        // If it does not exist, return false
        Answer answer = getAnswerById(answerId);
        if (answer == null) return false;

        Question question = getQuestionById(answer.getQuestionId());
        if (question == null) return false;


        String sql = "DELETE FROM answers WHERE answer_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, answerId);

            if (ps.executeUpdate() == 0){
                return false;
            }

            updateQuestionNumAnswers(question.getQuestionId());
            updateQuizScore(question.getQuizId());

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing answer from database", e);
        }
    }


    /**
     * Retrieves all answers for a given question, ordered by answer order.
     *
     * @param questionId the question ID
     * @return a list of answers for the question
     * @throws RuntimeException if a database error occurs
     */
    public List<Answer> getQuestionAnswers(long questionId) {
        String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY answer_order";

        List<Answer> answers = new ArrayList<>();

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    answers.add(retrieveAnswer(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying answers to a question from database", e);
        }

        return answers;
    }


    /**
     * Adds a new option text to an existing answer, appending it with a '¶' separator.
     *
     * @param answerId the ID of the answer to update
     * @param text     the new option text to append
     * @throws RuntimeException if a database error occurs
     */
    public boolean insertNewAnswerOption(long answerId, String text){
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Option text cannot be null or empty.");
        }

        String selectAnswers = "SELECT answer_text FROM answers WHERE answer_id = ?";
        String updateAnswerText = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectAnswers)){

            preparedStatement.setLong(1, answerId);

            String answers = "";

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    answers  = resultSet.getString(1);
                } else {
                    throw new IllegalArgumentException("Answer with ID " + answerId + " does not exist.");
                }
            }

            answers += "¶" + text;

            try (PreparedStatement ps = connection.prepareStatement(updateAnswerText)){
                ps.setString(1, String.join("¶", answers));
                ps.setLong(2, answerId);

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e){
            throw new RuntimeException("Database error while updating answer option text for answer ID: " + answerId, e);
        }
    }


    /**
     * Updates an existing answer option text for a given answer.
     *
     * @param answerId      the ID of the answer to update
     * @param oldAnswerText the existing option text to replace
     * @param newAnswerText the new option text
     * @throws RuntimeException if a database error occurs
     */
    public boolean updateAnswerOptionText(long answerId, String oldAnswerText, String newAnswerText){
        String selectAnswers = "SELECT answer_text FROM answers WHERE answer_id = ?";
        String updateAnswerText = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectAnswers)){

            preparedStatement.setLong(1, answerId);

            String answers = "";

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    answers  = resultSet.getString(1);
                } else {
                    throw new IllegalArgumentException("Answer with ID " + answerId + " does not exist.");
                }
            }

            List<String> eachAnswer = new ArrayList<>(Arrays.asList(answers.split("¶")));

            boolean found = false;

            for (int i = 0; i < eachAnswer.size(); i++) {
                if (eachAnswer.get(i).equalsIgnoreCase(oldAnswerText)) {
                    eachAnswer.set(i, newAnswerText);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException("Old answer text '" + oldAnswerText + "' not found in answer options.");
            }

            try (PreparedStatement ps = connection.prepareStatement(updateAnswerText)){
                ps.setString(1, String.join("¶", eachAnswer));
                ps.setLong(2, answerId);

                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer option text by id", e);
        }
    }


    /**
     * Removes a specific answer option from an answer.
     *
     * @param answerId   the ID of the answer to update
     * @param answerText the option text to remove
     * @throws RuntimeException if a database error occurs
     */
    public boolean removeAnswerOption(long answerId, String answerText){
        String selectAnswers = "SELECT answer_text FROM answers WHERE answer_id = ?";
        String updateAnswerText = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectAnswers)){

            preparedStatement.setLong(1, answerId);

            String answers = "";

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    answers  = resultSet.getString(1);
                } else {
                    throw new IllegalArgumentException("Answer with ID " + answerId + " does not exist.");
                }
            }

            List<String> eachAnswer = new ArrayList<>(Arrays.asList(answers.split("¶")));

            boolean found = false;

            for (int i = 0; i < eachAnswer.size(); i++) {
                if (eachAnswer.get(i).equalsIgnoreCase(answerText)) {
                    eachAnswer.remove(i);
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException("Option '" + answerText + "' not found in answer options.");
            }

            try (PreparedStatement ps = connection.prepareStatement(updateAnswerText)){
                ps.setString(1, String.join("¶", eachAnswer));
                ps.setLong(2, answerId);

                return ps.executeUpdate() > 0;
            }
        } catch(SQLException e){
            throw new RuntimeException("Error removing answer option text by id", e);
        }

    }


    /**
     * Sets exactly one correct answer for a multiple-choice question by:
     * <ul>
     *     <li>Marking all answers for the given question ID as incorrect</li>
     *     <li>Marking the specified answer (by its ID) as correct</li>
     * </ul>
     *
     * @param questionChoiceId the ID of the question of whose answers should be updated
     * @param choiceId the ID of the answer choice to be marked as correct
     * @return true if the operation was successful (i.e., at least one answer was marked false and exactly one was marked true); false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean setOneCorrectChoice(long questionChoiceId, long choiceId){
        String falseChoices = "UPDATE answers SET answer_validity = false WHERE question_id = ?";
        String setRightChoice = "UPDATE answers SET answer_validity = true WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement falseStmt = connection.prepareStatement(falseChoices);
            PreparedStatement rightStmt = connection.prepareStatement(setRightChoice)){

            falseStmt.setLong(1, questionChoiceId);
            int falseUpdated = falseStmt.executeUpdate();

            rightStmt.setLong(1, choiceId);
            int trueUpdated = rightStmt.executeUpdate();

            // true if at least one was set false and exactly one was set true
            return falseUpdated > 0 && trueUpdated == 1;
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer validity", e);
        }
    }


    /**
     * Sets the validity of an answer choice.
     *
     * @param choiceId  the answer ID to update
     * @param isCorrect true to mark as correct, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean setChoiceValidity(long choiceId, boolean isCorrect){

        // If it does not exist, return false
        Answer answer = getAnswerById(choiceId);
        if (answer == null) return false;

        Question question = getQuestionById(answer.getQuestionId());
        if (question == null) return false;


        String updateValidity = "UPDATE answers SET answer_validity = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateValidity)){

            updateStmt.setBoolean(1, isCorrect);
            updateStmt.setLong(2, choiceId);

            if (updateStmt.executeUpdate() > 0){
                long questionId = getQuestionId(choiceId);

                updateQuestionNumAnswers(questionId);
                updateQuizScore(getQuizId(questionId));

                return true;
            }

            return false;
        } catch(SQLException e) {
            throw new RuntimeException("Error setting choice's validity", e);
        }
    }


    /**
     * Updates the text of an answer by its ID.
     *
     * @param answerId the ID of the answer to update
     * @param newText  the new answer text
     * @throws RuntimeException if a database error occurs
     */
    public boolean updateAnswer(long answerId, String newText){
        String update = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(update)){

            updateStmt.setString(1, newText);
            updateStmt.setLong(2, answerId);

            return updateStmt.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer text", e);
        }
    }


    // --- Helper Methods ---


    /**
     * Retrieves an answer by its ID.
     *
     * @param answerId the ID of the question
     * @return the corresponding Answer object, or null if not found
     */
    private Answer getAnswerById(long answerId){
        String sql = "SELECT * FROM answers WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, answerId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveAnswer(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching answer by ID: " + answerId, e);
        }
    }

    /**
     * Converts the current row of the ResultSet to an Answer object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return an Answer object populated with the ResultSet data
     * @throws SQLException if a database access error occurs
     */
    private Answer retrieveAnswer(ResultSet rs) throws SQLException {
        return new Answer(rs.getLong("answer_id"), rs.getLong("question_id"),
                rs.getString("answer_text"), rs.getInt("answer_order"),
                rs.getBoolean("answer_validity"));
    }


}
