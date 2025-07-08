package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
create table answers(
    answer_id bigint primary key auto_increment,
    question_id bigint not null,
    answer_text text not null,
    answer_order int not null default 1,
    answer_validity boolean not null default true,

    foreign key (question_id) references questions(question_id) on delete cascade
);
 */
public class AnswersDao {
    private final BasicDataSource dataSource;
    private long cnt = 0;

    public AnswersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// throws RuntimeException if already exists question with same quizId and answerOrder
    public void insertAnswer(Answer answer) {
        String sql = "INSERT INTO answers (question_id, answer_text, answer_order, answer_validity) VALUES (?,?, ?, ?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswerText());
            ps.setInt(3, answer.getAnswerOrder());
            ps.setBoolean(4, answer.getAnswerValidity());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    cnt++;
                    answer.setAnswerId(rs.getLong(1));
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting answer into database", e);
        }
    }

    public void removeAnswer(long answerId) {
        String sql = "DELETE FROM answers WHERE answer_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, answerId);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing answer from database", e);
        }
    }

    // TODO DELETE
    /// returns null if answer is not present in table
    public Answer getAnswerById(long id) {
        String sql = "SELECT * FROM answers where answer_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next())
                    return retrieveAnswer(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying answer by id from database", e);
        }

        return null;
    }

    /// returns empty list if no answers found
    public ArrayList<Answer> getQuestionAnswers(long questionId) {
        String sql = "SELECT * FROM answers WHERE question_id = ? ORDER BY answer_order";

        ArrayList<Answer> answers = new ArrayList<>();

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

    public boolean contains(Answer answer) {
        if(answer == null){
            return false;
        }

        String sql = "SELECT COUNT(*) FROM answers WHERE answer_id = ? AND question_id=? " +
                "AND answer_text=? AND answer_order = ? " +
                "AND answer_validity = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, answer.getAnswerId());
            ps.setLong(2, answer.getQuestionId());
            ps.setString(3, answer.getAnswerText());
            ps.setInt(4, answer.getAnswerOrder());
            ps.setBoolean(5, answer.getAnswerValidity());

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

    public void insertNewAnswerOption(long answerId, String text){
        String selectAnswers = "SELECT answer_text FROM answers WHERE answer_id = ?";
        String answers = "";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectAnswers)){

            preparedStatement.setLong(1, answerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    answers  = resultSet.getString(1);
                }
            }

            answers += "/" + text;

            String updateAnswerText = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateAnswerText)){
                ps.setString(1, String.join("/", answers));
                ps.setLong(2, answerId);

                ps.executeUpdate();
            }
        } catch(SQLException e){
            throw new RuntimeException("Error updating answer option text by id", e);
        }
    }

    public void updateAnswerOptionText(long answerId, String oldAnswerText, String newAnswerText){
        String selectAnswers = "SELECT answer_text FROM answers WHERE answer_id = ?";
        String answers = "";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(selectAnswers)){

            preparedStatement.setLong(1, answerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    answers  = resultSet.getString(1);
                }
            }

            List<String> eachAnswer = new ArrayList<>(Arrays.asList(answers.split("/")));

            for (int i = 0; i < eachAnswer.size(); i++) {
                if (eachAnswer.get(i).equalsIgnoreCase(oldAnswerText)) {
                    eachAnswer.set(i, newAnswerText);
                    break;
                }
            }

            String updateAnswerText = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateAnswerText)){
                ps.setString(1, String.join("/", eachAnswer));
                ps.setLong(2, answerId);

                ps.executeUpdate();
            }
        } catch(SQLException e){
            throw new RuntimeException("Error updating answer option text by id", e);
        }
    }

    public void removeAnswerOption(long answerId, String answerText){
        String selectAnswers = "SELECT answer_text FROM answers WHERE answer_id = ?";
        String answers = "";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectAnswers)){

            preparedStatement.setLong(1, answerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    answers  = resultSet.getString(1);
                }
            }

            List<String> eachAnswer = new ArrayList<>(Arrays.asList(answers.split("/")));

            for (int i = 0; i < eachAnswer.size(); i++) {
                if (eachAnswer.get(i).equalsIgnoreCase(answerText)) {
                    eachAnswer.remove(i);
                    break;
                }
            }

            String updateAnswerText = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

            try (PreparedStatement ps = connection.prepareStatement(updateAnswerText)){
                ps.setString(1, String.join("/", eachAnswer));
                ps.setLong(2, answerId);

                ps.executeUpdate();
            }
        } catch(SQLException e){
            throw new RuntimeException("Error removing answer option text by id", e);
        }

    }

    public void setOneCorrectChoice(long questionChoiceId, long choiceId){
        String falseChoices = "UPDATE answers SET answer_validity = false WHERE question_id = ?";
        String setRightChoice = "UPDATE answers SET answer_validity = true WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement falseStmt = connection.prepareStatement(falseChoices);
            PreparedStatement rightStmt = connection.prepareStatement(setRightChoice)){

            falseStmt.setLong(1, questionChoiceId);

            falseStmt.executeUpdate();

            rightStmt.setLong(1, choiceId);

            rightStmt.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer validity", e);
        }
    }

    public void setChoiceValidity(long choiceId, boolean isCorrect){
        String updateValidity = "UPDATE answers SET answer_validity = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(updateValidity)){

            updateStmt.setBoolean(1, isCorrect);
            updateStmt.setLong(2, choiceId);

            updateStmt.executeUpdate();
        } catch(SQLException e) {
            throw new RuntimeException("Error setting choice's validity", e);
        }
    }

    public void updateAnswer(long answerId, String newText){
        String update = "UPDATE answers SET answer_text = ? WHERE answer_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStmt = connection.prepareStatement(update)){

            System.out.println(newText);

            updateStmt.setString(1, newText);
            updateStmt.setLong(2, answerId);

            updateStmt.executeUpdate();
        } catch (SQLException e){
            throw new RuntimeException("Error updating answer text", e);
        }
    }

    public long getCount(){
        return cnt;
    }
    private Answer retrieveAnswer(ResultSet rs) throws SQLException {
        return new Answer(rs.getLong("answer_id"), rs.getLong("question_id"),
                rs.getString("answer_text"), rs.getInt("answer_order"),
                rs.getBoolean("answer_validity"));
    }
}
