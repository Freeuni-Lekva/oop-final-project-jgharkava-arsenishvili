package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    private long cnt=0;
    public AnswersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertAnswer(Answer answer) {
//        if(contains(answer)) {
//            return;
//        }
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
        if(getAnswerById(answerId)==null){
            return;
        }
        String sql = "DELETE FROM answers WHERE answer_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, answerId);

            ps.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing answer from database", e);
        }
    }

    // TO DELETE
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
        if(answer==null){
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
    public long getCount(){
        return cnt;
    }
    private Answer retrieveAnswer(ResultSet rs) throws SQLException {
        return new Answer(rs.getLong("answer_id"), rs.getLong("question_id"),
                rs.getString("answer_text"), rs.getInt("answer_order"),
                rs.getBoolean("answer_validity"));
    }
}
