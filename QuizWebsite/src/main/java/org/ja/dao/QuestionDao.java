package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.quiz.question.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuestionDao {
    /*
    create table questions(
    question_id bigint primary key auto_increment,
    quiz_id bigint not null,
    question text not null,
    image_url varchar(256) default null,
    question_type enum('question-response', 'fill-in-the-blank', 'multiple-choice', 'picture-response',
       'multi-answer', 'multi-choice-multi-answers', 'matching') not null,

    num_answers int not null default 1,
    order_status enum('unordered', 'ordered') not null default 'ordered',
     */
    private final BasicDataSource dataSource;
    private long cnt=0;
    public QuestionDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertQuestion(Question question) {
        String sql = "INSERT INTO questions ( quiz_id, question, image_url, " +
                "question_type, num_answers, order_status) VALUES (?,?, ?, ?, ?, ?);";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getImageUrl());
            ps.setString(4, question.getQuestionType());
            ps.setInt(5, question.getNumAnswers());
            ps.setString(6, question.getOrderStatus());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    cnt++;
                    question.setQuestionId(rs.getLong("question_id"));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Questions into database", e);
        }
    }

    public void removeQuestion(long questionId) {
        String sql = "DELETE FROM questions WHERE quiz_id = ?";

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, questionId);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing question from database", e);
        }
    }

    public Question getQuestionById(long questionId) {
        String sql = "SELECT * FROM questions WHERE question_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return retrieveQuestion(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying question by id from database", e);
        }

        return null;
    }

    public ArrayList<Question> getQuizQuestions(long quizId) {
        ArrayList<Question> questions = new ArrayList<>();

        String sql = "SELECT * FROM questions WHERE quiz_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    questions.add(retrieveQuestion(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz questions from database", e);
        }

        return questions;
    }

    public void updateQuestion(Question question){
        String sql = "UPDATE questions SET quiz_Id=?, question = ?," +
                "image_url=?, question_type=?, num_answers=?," +
                "order_status=? WHERE question_id = ?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){
            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getImageUrl());
            ps.setString(4, question.getQuestionType());
            ps.setInt(5, question.getNumAnswers());
            ps.setString(6, question.getOrderStatus());
            ps.setLong(7, question.getQuestionId());

           ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating question into database", e);
        }
    }

    public boolean contains(Question question){
        String sql = "SELECT COUNT(*) FROM questions WHERE quiz_id = ? AND question=?" +
                "AND image_url=? AND question_type = ?" +
                " AND num_answers = ? AND order_status = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getImageUrl());
            ps.setString(4, question.getQuestionType());
            ps.setInt(5, question.getNumAnswers());
            ps.setString(6, question.getOrderStatus());

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

    private Question retrieveQuestion(ResultSet rs) throws SQLException {
        return new Question(rs.getLong("question_id"), rs.getLong("quiz_id"),
                rs.getString("question"), rs.getString("image_url"),
                rs.getString("question_type"), rs.getInt("num_answers"),
                rs.getString("order_status"));
    }
}
