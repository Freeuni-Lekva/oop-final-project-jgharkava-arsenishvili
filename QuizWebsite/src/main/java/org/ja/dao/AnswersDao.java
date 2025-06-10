package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    BasicDataSource dataSource;
    public AnswersDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertAnswer(Answer answer) {
        String sql="INSERT INTO answers (question_id, answer_text, answer_order, answer_validity) VALUES (?,?, ?, ?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, answer.getQuestionId());
            ps.setString(2, answer.getAnswer_text());
            ps.setInt(3, answer.getAnswer_order());
            ps.setBoolean(4, answer.getAnswer_validity());
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                answer.setAnswerId(rs.getLong(1));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeAnswer(long answerId) {
        String sql="DELETE FROM answers WHERE answer_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, answerId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public Answer getAnswerById(int id) {
        String sql="SELECT * FROM answers where answer_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                Answer answer=new Answer();
                answer.setAnswerId(rs.getLong(1));
                answer.setQuestionId(rs.getLong("question_id"));
                answer.setAnswer_text(rs.getString("answer_text"));
                answer.setAnswer_order(rs.getInt("answer_order"));
                answer.setAnswer_validity(rs.getBoolean("answer_validity"));
                return answer;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    public ArrayList<Answer> getQuestionAnswers(long questionId) {
        String sql="SELECT * FROM answers WHERE question_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, questionId);
            ps.executeUpdate();
            ResultSet rs=ps.executeQuery();
            ArrayList<Answer> answers=new ArrayList<>();
            while(rs.next()){
                Answer answer=new Answer();
                answer.setAnswerId(rs.getLong(1));
                answer.setQuestionId(rs.getLong("question_id"));
                answer.setAnswer_text(rs.getString("answer_text"));
                answer.setAnswer_order(rs.getInt("answer_order"));
                answer.setAnswer_validity(rs.getBoolean("answer_validity"));
                answers.add(answer);
            }
            return answers;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
