package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.quiz.Quiz;
import org.ja.model.user.User;

import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class QuizzesDao {
    private BasicDataSource dataSource;
    public QuizzesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertQuiz(Quiz quiz) {
        String sql="INSERT INTO quizzes ( quiz_name, quiz_description, average_rating, " +
                "participant_count, creation_date, time_limit_in_minutes, category_id," +
                "creator_id, question_order_status, question_placement_status," +
                "question_correction_status) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, quiz.getName());
            ps.setString(2, quiz.getDescription());
            ps.setFloat(3, quiz.getAvgRating());
            ps.setLong(4, quiz.getParticipantCount());
            ps.setString(5, quiz.getCreationDate());
            ps.setInt(6, quiz.getTimeInMinutes());
            ps.setLong(7, quiz.getCategoryId());
            ps.setLong(8, quiz.getCreatorId());
            ps.setString(9,quiz.getQuestionOrder());
            ps.setString(10, quiz.getQuestionPlacement());
            ps.setString(11, quiz.getQuestionCorrection());
            ps.executeUpdate();
            ResultSet rs=ps.getGeneratedKeys();
            if(rs.next()){
                quiz.setId(rs.getLong(1));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void removeQuizById(long id) {
        String sql="DELETE FROM quizzes WHERE quiz_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setLong(1, id);

            preparedStatement.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeQuizByName(String name) {
        String sql="DELETE FROM quizzes WHERE quiz_name=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement preparedStatement = c.prepareStatement(sql);
            preparedStatement.setString(1, name);

            preparedStatement.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void updateQuizById(Quiz quiz, long id) {
        String sql = "UPDATE quizzes SET quiz_name = ?, quiz_description=?, " +
                "average_rating=?, participant_count=?, creation_date=?, " +
                "time_limit_in_minutes=?, category_id=?, creator_id=?," +
                "question_order_status=?, question_placement_status=?, " +
                "question_correction_status=? WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, quiz.getName());
            stmt.setString(2, quiz.getDescription());
            stmt.setDouble(3, quiz.getAvgRating());
            stmt.setLong(4, quiz.getParticipantCount());
            stmt.setString(5, quiz.getCreationDate());
            stmt.setInt(6, quiz.getTimeInMinutes());
            stmt.setLong(7, quiz.getCategoryId());
            stmt.setLong(8, quiz.getCreatorId());   // "one-page" or "multiple-page"
            stmt.setString(9, quiz.getQuestionOrder());  // "immediate-correction" or "final-correction"
            stmt.setString(10, quiz.getQuestionPlacement());
            stmt.setString(11, quiz.getQuestionCorrection());
            stmt.setLong(12, id);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateQuizByName(Quiz quiz, String name) {
        String sql = "UPDATE quizzes SET quiz_name = ?, quiz_description=?, " +
                "average_rating=?, participant_count=?, creation_date=?, " +
                "time_limit_in_minutes=?, category_id=?, creator_id=?," +
                "question_order_status=?, question_placement_status=?, " +
                "question_correction_status=? WHERE quiz_name = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, quiz.getName());
            stmt.setString(2, quiz.getDescription());
            stmt.setDouble(3, quiz.getAvgRating());
            stmt.setLong(4, quiz.getParticipantCount());
            stmt.setString(5, quiz.getCreationDate());
            stmt.setInt(6, quiz.getTimeInMinutes());
            stmt.setLong(7, quiz.getCategoryId());
            stmt.setLong(8, quiz.getCreatorId());   // "one-page" or "multiple-page"
            stmt.setString(9, quiz.getQuestionOrder());  // "immediate-correction" or "final-correction"
            stmt.setString(10, quiz.getQuestionPlacement());
            stmt.setString(11, quiz.getQuestionCorrection());
            stmt.setString(12, name);
            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Quiz> filterQuizzes(Filter filter) {
        String sql="SELECT * FROM users WHERE "+filter.toString();
        ArrayList<Quiz> ans=new ArrayList<>();
        try(Connection c=dataSource.getConnection()){
            PreparedStatement st=c.prepareStatement(sql);
            ResultSet rs=st.executeQuery();
            while(rs.next()){
                long nid=rs.getLong("quiz_id");
                String quizName=rs.getString("quiz_name");
                String quizDescription=rs.getString("quiz_description");
                float avgRating=rs.getFloat("average_rating");
                int participantCount=rs.getInt("participant_count");
                String creationDate=rs.getString("creation_date");
                int timeLimitInMinutes=rs.getInt("time_limit_in_minutes");
                long categoryId=rs.getLong("category_id");
                long creatorId=rs.getLong("creator_id");
                String questionOrderStatus=rs.getString("question_order_status");
                String questionPlacementStatus=rs.getString("question_placement_status");
                String questionCorrectionStatus=rs.getString("question_correction_status");
                Quiz newQuiz=new Quiz(nid, quizName,quizDescription, avgRating, participantCount, creationDate,
                        timeLimitInMinutes, categoryId, creatorId, questionOrderStatus,
                        questionPlacementStatus, questionCorrectionStatus);
                ans.add(newQuiz);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
        /*
        quiz_id bigint primary key auto_increment,
    quiz_name varchar(64) unique not null,
    quiz_description text,
    average_rating double not null default 0,
    participant_count bigint not null default 0,
    creation_date timestamp default current_timestamp,
    time_limit_in_minutes int not null default 0,
    category_id bigint not null,
    creator_id bigint not null,
    question_order_status enum('ordered', 'randomized') not null default 'ordered',
    question_placement_status enum('one-page', 'multiple-page') not null default 'one-page',
    question_correction_status enum('immediate-correction', 'final-correction')
        not null default 'final-correction',
         */
    }
    public ArrayList<Quiz> getQuizzesSortedByCreationDate() {
        String sql = "SELECT * FROM quizzes ORDER BY creation_date DESC";
        ArrayList<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getLong("quiz_id"));
                quiz.setName(rs.getString("quiz_name"));
                quiz.setDescription(rs.getString("quiz_description"));
                quiz.setAvgRating(rs.getFloat("average_rating"));
                quiz.setParticipantCount(rs.getLong("participant_count"));
                quiz.setCreationDate(rs.getString("creation_date"));
                quiz.setTimeInMinutes(rs.getInt("time_limit_in_minutes"));
                quiz.setCategoryId(rs.getLong("category_id"));
                quiz.setCreatorId(rs.getLong("creator_id"));
                quiz.setQuestionOrder(rs.getString("question_order_status"));
                quiz.setQuestionPlacement(rs.getString("question_placement_status"));
                quiz.setQuestionCorrection(rs.getString("question_correction_status"));

                quizzes.add(quiz);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return quizzes;
    }
    public ArrayList<Quiz> getQuizzesSortedByParticipantCount() {
        String sql = "SELECT * FROM quizzes ORDER BY participant_count DESC";
        ArrayList<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Quiz quiz = new Quiz();
                quiz.setId(rs.getLong("quiz_id"));
                quiz.setName(rs.getString("quiz_name"));
                quiz.setDescription(rs.getString("quiz_description"));
                quiz.setAvgRating(rs.getFloat("average_rating"));
                quiz.setParticipantCount(rs.getLong("participant_count"));
                quiz.setCreationDate(rs.getString("creation_date"));
                quiz.setTimeInMinutes(rs.getInt("time_limit_in_minutes"));
                quiz.setCategoryId(rs.getLong("category_id"));
                quiz.setCreatorId(rs.getLong("creator_id"));
                quiz.setQuestionOrder(rs.getString("question_order_status"));
                quiz.setQuestionPlacement(rs.getString("question_placement_status"));
                quiz.setQuestionCorrection(rs.getString("question_correction_status"));

                quizzes.add(quiz);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return quizzes;
    }

}
