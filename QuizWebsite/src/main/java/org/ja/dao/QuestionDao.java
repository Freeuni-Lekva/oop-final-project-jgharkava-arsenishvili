package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.quiz.question.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private BasicDataSource dataSource;
    public QuestionDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertQuestion(Question question) {
        String sql="INSERT INTO questions ( quiz_id, question, image_url, " +
                "question_type, num_answers, order_status,) VALUES (?,?, ?, ?, ?, ?);";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, question.getQuizId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getImageUrl());
            ps.setString(4, question.getQuestionType());
            ps.setInt(5, question.getNumAnswers());
            ps.setString(6, question.getOrderStatus());
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                question.setQuestionId(rs.getLong(1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void removeQuestion(long questionId) {
        String sql="DELETE FROM questions WHERE quiz_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, questionId);
            ps.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public Question getQuestionById(long id) {
        String sql="SELECT * FROM questions WHERE quiz_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, id);
            ResultSet rs=ps.executeQuery();
            if(rs.next()){
                long nid=rs.getLong("question_id");
                long nQuizId=rs.getLong("quiz_id");
                String nQuestionText=rs.getString("question");
                String nImageUrl=rs.getString("image_url");
                String nQuestionType=rs.getString("question_type");
                int nNumAnswers=rs.getInt("num_answers");
                String nOrderStatus=rs.getString("order_status");
                Question newQuestion=new Question(nid, nQuizId, nQuestionText, nImageUrl, nQuestionType, nNumAnswers,  nOrderStatus);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<Question> getQuizQuestions(long quizId) {
        String sql="SELECT * FROM questions WHERE quiz_id="+quizId;
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ResultSet rs=ps.executeQuery();
            ArrayList<Question> questions=new ArrayList<>();
            while(rs.next()){
                long nid=rs.getLong("question_id");
                long nQuizId=rs.getLong("quiz_id");
                String nQuestionText=rs.getString("question");
                String nImageUrl=rs.getString("image_url");
                String nQuestionType=rs.getString("question_type");
                int nNumAnswers=rs.getInt("num_answers");
                String nOrderStatus=rs.getString("order_status");
                Question tmp=new Question(nid, nQuizId, nQuestionText, nImageUrl, nQuestionType, nNumAnswers,  nOrderStatus);
                questions.add(tmp);
            }
            return questions;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
