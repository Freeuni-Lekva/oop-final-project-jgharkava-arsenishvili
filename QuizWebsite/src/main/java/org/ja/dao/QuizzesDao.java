package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.quiz.Quiz;
import org.ja.model.user.User;

import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class QuizzesDao {
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

    private final BasicDataSource dataSource;

    public QuizzesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes ( quiz_name, quiz_description, average_rating, " +
                "participant_count, creation_date, time_limit_in_minutes, category_id," +
                "creator_id, question_order_status, question_placement_status," +
                "question_correction_status) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){

            ps.setString(1, quiz.getName());
            ps.setString(2, quiz.getDescription());
            ps.setFloat(3, quiz.getAvgRating());
            ps.setLong(4, quiz.getParticipantCount());
            ps.setTimestamp(5, quiz.getCreationDate());
            ps.setInt(6, quiz.getTimeInMinutes());
            ps.setLong(7, quiz.getCategoryId());
            ps.setLong(8, quiz.getCreatorId());
            ps.setString(9,quiz.getQuestionOrder());
            ps.setString(10, quiz.getQuestionPlacement());
            ps.setString(11, quiz.getQuestionCorrection());

            ps.executeUpdate();

            try(ResultSet rs=ps.getGeneratedKeys()){
                if(rs.next())
                    quiz.setId(rs.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz into database", e);
        }
    }

    public void removeQuizById(long id) {
        String sql = "DELETE FROM quizzes WHERE quiz_id=?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setLong(1, id);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz by id from database", e);
        }
    }

    public void removeQuizByName(String name) {
        String sql = "DELETE FROM quizzes WHERE quiz_name=?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setString(1, name);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz by name from database", e);
        }
    }

    // TO DELETE???
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
            stmt.setTimestamp(5, quiz.getCreationDate());
            stmt.setInt(6, quiz.getTimeInMinutes());
            stmt.setLong(7, quiz.getCategoryId());
            stmt.setLong(8, quiz.getCreatorId());   // "one-page" or "multiple-page"
            stmt.setString(9, quiz.getQuestionOrder());  // "immediate-correction" or "final-correction"
            stmt.setString(10, quiz.getQuestionPlacement());
            stmt.setString(11, quiz.getQuestionCorrection());
            stmt.setLong(12, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz by id into database", e);
        }
    }

    // TO DELETE
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
            stmt.setTimestamp(5, quiz.getCreationDate());
            stmt.setInt(6, quiz.getTimeInMinutes());
            stmt.setLong(7, quiz.getCategoryId());
            stmt.setLong(8, quiz.getCreatorId());   // "one-page" or "multiple-page"
            stmt.setString(9, quiz.getQuestionOrder());  // "immediate-correction" or "final-correction"
            stmt.setString(10, quiz.getQuestionPlacement());
            stmt.setString(11, quiz.getQuestionCorrection());
            stmt.setString(12, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz by name into database", e);
        }
    }

    public ArrayList<Quiz> filterQuizzes(Filter filter) {
        String sql="SELECT * FROM users WHERE " + filter.toString();

        ArrayList<Quiz> quizzes = new ArrayList<>();

        try(Connection c = dataSource.getConnection();
            PreparedStatement st = c.prepareStatement(sql)){

            ResultSet rs = st.executeQuery();

            while(rs.next())
                quizzes.add(retrieveQuiz(rs));

        } catch (SQLException e) {
            throw new RuntimeException("Error filtering quizzes from database", e);
        }

        return quizzes;
    }


    public ArrayList<Quiz> getQuizzesSortedByCreationDate() {
        String sql = "SELECT * FROM quizzes ORDER BY creation_date DESC";
        ArrayList<Quiz> quizzes = new ArrayList<>();

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

    //  TO DELETE: probably not useful due to filter.
    public ArrayList<Quiz> getQuizzesSortedByParticipantCount() {
        String sql = "SELECT * FROM quizzes ORDER BY participant_count DESC";
        ArrayList<Quiz> quizzes = new ArrayList<>();

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


    private Quiz retrieveQuiz(ResultSet rs) throws SQLException {
        return new Quiz(rs.getLong("quiz_id"), rs.getString("quiz_name"),
                rs.getString("quiz_description"), rs.getFloat("average_rating"),
                rs.getLong("participant_count"), rs.getTimestamp("creation_date"),
                rs.getInt("time_limit_in_minutes"), rs.getLong("category_id"),
                rs.getLong("creator_id"), rs.getString("question_order_status"),
                rs.getString("question_placement_status"), rs.getString("question_correction_status"));
    }

}
