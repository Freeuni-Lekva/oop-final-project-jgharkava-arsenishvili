package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.History;
import org.ja.model.OtherObjects.QuizRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
create table quiz_rating(
    quiz_id bigint not null,
    user_id bigint not null,
    rating tinyint not null check (rating between 0 and 5),
    review text,

    primary key (quiz_id, user_id),
    foreign key (quiz_id) references quizzes(quiz_id) on delete cascade,
    foreign key (user_id) references users(user_id) on delete cascade
)
 */
public class QuizRatingsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public QuizRatingsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertQuizRating(QuizRating qr){
        if(contains(qr)){
            return;
        }
        if(contains(qr.getQuizId(), qr.getUserId())){
            updateQuizRating(qr);
            return;
        }
        String sql = "INSERT INTO quiz_rating (quiz_id, user_id, rating, review) VALUES (?,?,?,?)";
                //"ON DUPLICATE KEY UPDATE rating = VALUES(rating), review = VALUES(review)";
        //String sql="MERGE INTO quiz_rating KEY(quiz_id, user_id)"+
        //"VALUES (?, ?, ?, ?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, qr.getQuizId());
            ps.setLong(2, qr.getUserId());
            ps.setInt(3, qr.getRating());
            ps.setString(4, qr.getReview());
            ps.executeUpdate();
            cnt++;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz rating into database", e);
        }
    }

    public void updateQuizRating(QuizRating qr){
        String sql = "UPDATE quiz_rating SET rating=?, review=? WHERE quiz_id=? AND user_id=?";

        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(3, qr.getQuizId());
            ps.setLong(4, qr.getUserId());
            ps.setInt(1, qr.getRating());
            ps.setString(2, qr.getReview());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz rating into database", e);
        }
    }
    public void removeQuizRating(long quizId, long userId){
        if(!contains(quizId, userId)){
            return;
        }
        String sql = "DELETE FROM quiz_rating WHERE quiz_id = ? AND user_id = ?";
        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);
            ps.setLong(2, userId);

            ps.executeUpdate();
            cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz rating from database", e);
        }
    }

    public ArrayList<QuizRating> getQuizRatingsByUserId(long userId){
        ArrayList<QuizRating> quizRatings = new ArrayList<>();

        String sql = "SELECT * FROM quiz_rating WHERE user_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()){
                while(rs.next())
                    quizRatings.add(retrieveQuizRating(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz rating by user id from database", e);
        }

        return quizRatings;
    }

    public ArrayList<QuizRating> getQuizRatingsByQuizId(long quizId){
        ArrayList<QuizRating> quizRatings = new ArrayList<>();

        String sql = "SELECT * FROM quiz_rating WHERE quiz_id = ?";

        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    quizRatings.add(retrieveQuizRating(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz rating by quiz id from database", e);
        }

        return quizRatings;
    }
    public boolean contains(QuizRating qr){
        if(qr==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM quiz_rating WHERE quiz_id=?" +
                "AND user_id=? AND rating=? AND review=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, qr.getQuizId());
            ps.setLong(2,qr.getUserId());
            ps.setDouble(3, qr.getRating());
            ps.setString(4,qr.getReview());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public boolean contains(long qid, long uid){

        String sql = "SELECT COUNT(*) FROM quiz_rating WHERE quiz_id = ? AND user_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, qid);
            ps.setLong(2, uid);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public long getCount(){
        return cnt;
    }
    private QuizRating retrieveQuizRating(ResultSet rs) throws SQLException {
        return new QuizRating(rs.getLong("quiz_id"), rs.getLong("user_id"),
                rs.getInt("rating"), rs.getString("review"));
    }
}
