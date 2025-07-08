package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.Filters.Filter;
import org.ja.model.quiz.Quiz;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QuizzesDao {
    private final BasicDataSource dataSource;
    private long cnt=  0;
    public QuizzesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertQuiz(Quiz quiz) {
        String sql = "INSERT INTO quizzes ( quiz_name, quiz_description, quiz_score, average_rating, " +
                "participant_count, time_limit_in_minutes, category_id," +
                "creator_id, question_order_status, question_placement_status," +
                "question_correction_status ) VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            ps.setString(1, quiz.getName());
            ps.setString(2, quiz.getDescription());
            ps.setInt(3, quiz.getScore());
            ps.setDouble(4, quiz.getAvgRating());
            ps.setLong(5, quiz.getParticipantCount());
            ps.setInt(6, quiz.getTimeInMinutes());
            ps.setLong(7, quiz.getCategoryId());
            ps.setLong(8, quiz.getCreatorId());
            ps.setString(9,quiz.getQuestionOrder());
            ps.setString(10, quiz.getQuestionPlacement());
            ps.setString(11, quiz.getQuestionCorrection());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) {
                    cnt++;

                    long quizId = rs.getLong(1);
                    quiz.setId(quizId);

                    String s = "SELECT creation_date FROM quizzes where quiz_id = ?";

                    try (PreparedStatement preparedStatement = c.prepareStatement(s)){
                        preparedStatement.setLong(1, quizId);

                        try (ResultSet r = preparedStatement.executeQuery()) {
                            if (r.next()) {
                                quiz.setCreationDate(r.getTimestamp("creation_date"));
                            }
                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz into database", e);
        }
    }

    public void removeQuizById(long id) {
        String sql = "DELETE FROM quizzes WHERE quiz_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement preparedStatement = c.prepareStatement(sql)){

            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz by id from database", e);
        }
    }

    // TODO update function
    public void removeQuizByName(String name) {
        String sql = "DELETE FROM quizzes WHERE quiz_name = ?";
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz by name from database", e);
        }
    }

    public void updateQuiz(Quiz quiz){
        String sql = "UPDATE quizzes SET quiz_name = ?, quiz_description=?, " +
                "average_rating=?, participant_count=?, creation_date=?, " +
                "time_limit_in_minutes=?, category_id=?, creator_id=?," +
                "question_order_status=?, question_placement_status=?, " +
                "question_correction_status=?, quiz_score = ? WHERE quiz_id = ?";

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
            stmt.setInt(12, quiz.getScore());
            stmt.setLong(13, quiz.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz in database", e);
        }
    }

    // TO DELETE
    public void updateQuizById(Quiz quiz, long id) {
        String sql = "UPDATE quizzes SET quiz_name = ?, quiz_description=?, " +
                "average_rating=?, participant_count=?," +
                "time_limit_in_minutes=?, category_id=?, creator_id=?," +
                "question_order_status=?, question_placement_status=?, " +
                "question_correction_status=?, quiz_score = ? WHERE quiz_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, quiz.getName());
            stmt.setString(2, quiz.getDescription());
            stmt.setDouble(3, quiz.getAvgRating());
            stmt.setLong(4, quiz.getParticipantCount());
            stmt.setInt(5, quiz.getTimeInMinutes());
            stmt.setLong(6, quiz.getCategoryId());
            stmt.setLong(7, quiz.getCreatorId());   // "one-page" or "multiple-page"
            stmt.setString(8, quiz.getQuestionOrder());  // "immediate-correction" or "final-correction"
            stmt.setString(9, quiz.getQuestionPlacement());
            stmt.setString(10, quiz.getQuestionCorrection());
            stmt.setInt(11, quiz.getScore());
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
                "question_correction_status=?, quiz_score = ? WHERE quiz_name = ?";

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
            stmt.setInt(12, quiz.getScore());
            stmt.setString(13, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz by name into database", e);
        }
    }

    public ArrayList<Quiz> filterQuizzes(Filter filter) {
        String sql = "SELECT DISTINCT quizzes.quiz_id, quiz_name, quiz_description, average_rating, " +
                "participant_count, creation_date, time_limit_in_minutes, quizzes.category_id, " +
                "creator_id, question_order_status, question_placement_status, question_correction_status, quiz_score " +
                "FROM quizzes left join categories on categories.category_id = quizzes.category_id " +
                "left join quiz_tag on quizzes.quiz_id = quiz_tag.quiz_id " +
                "left join tags on tags.tag_id = quiz_tag.tag_id " +
                "WHERE " + filter.buildWhereClause() + " ORDER BY " + filter.buildOrderByClause();

        ArrayList<Quiz> quizzes = new ArrayList<>();

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            List<Object> parameters = filter.getParameters();

            for (int i = 0; i < parameters.size(); i++){
                ps.setObject(i + 1, parameters.get(i));
            }

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    quizzes.add(retrieveQuiz(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error filtering quizzes from database", e);
        }

        return quizzes;
    }


    // TO DO: change name
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

    public ArrayList<Quiz> getFriendsQuizzesSortedByCreationDate(long userId) {
        String sql = "SELECT q.*" +
                "        FROM quizzes q" +
                "        JOIN (" +
                "            SELECT" +
                "                CASE" +
                "                    WHEN first_user_id = ? THEN second_user_id" +
                "                    ELSE first_user_id" +
                "                END AS friend_id" +
                "            FROM friendships" +
                "            WHERE (first_user_id = ? OR second_user_id = ?)" +
                "              AND friendship_status = 'friends'" +
                "        ) f ON q.creator_id = f.friend_id" +
                "        ORDER BY q.creation_date DESC";
        ArrayList<Quiz> quizzes = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
             stmt.setLong(1, userId);
             stmt.setLong(2, userId);
             stmt.setLong(3, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()){
                    quizzes.add(retrieveQuiz(rs));
                }
            }

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
    
    public boolean containsQuiz(String name) {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE quiz_name = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }

                return false;
            } catch (SQLException e) {
                throw new RuntimeException("Error checking user existence", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean containsQuiz(long id) {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE quiz_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
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

    public Quiz getQuizById(long id) {
        String sql = "SELECT * FROM quizzes WHERE quiz_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id); // Properly set the parameter
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveQuiz(rs); // Move to the first row and extract data
                } else {
                    return null; // or throw an exception if appropriate
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving quiz by ID", e);
        }
    }

    public Quiz getQuizByName(String name) {
        String sql = "SELECT * FROM quizzes WHERE quiz_name = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, name); // Properly set the parameter
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveQuiz(rs); // Move to the first row and extract data
                } else {
                    return null; // or throw an exception if appropriate
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving quiz by name", e);
        }
    }

    public ArrayList<Quiz> getQuizzesByCreatorId(long id) {
        String sql = "SELECT * FROM quizzes WHERE creator_id = ?";
        ArrayList<Quiz> quizzes = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){
             stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    quizzes.add(retrieveQuiz(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error querying quizzes by creator id", e);
        }
        return quizzes;
    }

    private Quiz retrieveQuiz(ResultSet rs) throws SQLException {
        return new Quiz(rs.getLong("quiz_id"), rs.getString("quiz_name"),
                rs.getString("quiz_description"), rs.getInt("quiz_score"),
                rs.getDouble("average_rating"),
                rs.getLong("participant_count"), rs.getTimestamp("creation_date"),
                rs.getInt("time_limit_in_minutes"), rs.getLong("category_id"),
                rs.getLong("creator_id"), rs.getString("question_order_status"),
                rs.getString("question_placement_status"), rs.getString("question_correction_status"));
    }

}
