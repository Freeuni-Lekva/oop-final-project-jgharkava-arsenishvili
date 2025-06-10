package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.QuizRating;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    private BasicDataSource dataSource;
    public QuizRatingsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertQuizRating(QuizRating qr){
        String sql="INSERT INTO quiz_rating (quiz_id, user_id, rating, review) VALUES (?,?,?,?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ps.setLong(1, qr.getQuizId());
            ps.setLong(2, qr.getUserId());
            ps.setDouble(3, qr.getRating());
            ps.setString(4, qr.getReview());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeQuizRating(long quizId, long userId){
        String sql="DELETE FROM quiz_rating WHERE quiz_id="+quizId+" AND user_id="+userId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<QuizRating> getQuizRatingsByUserId(long userId){
        String sql="SELECT * FROM quiz_rating WHERE user_id="+userId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<QuizRating> quizRatings = new ArrayList<>();
            while(rs.next()){
                QuizRating quizRating = new QuizRating();
                quizRating.setQuizId(rs.getLong("quiz_id"));
                quizRating.setUserId(rs.getLong("user_id"));
                quizRating.setRating(rs.getInt("rating"));
                quizRating.setReview(rs.getString("review"));
                quizRatings.add(quizRating);
            }
            return quizRatings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<QuizRating> getQuizRatingsByQuizId(long quizId){
        String sql="SELECT * FROM quiz_rating WHERE quiz_id="+quizId;
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ArrayList<QuizRating> quizRatings = new ArrayList<>();
            while(rs.next()){
                QuizRating quizRating = new QuizRating();
                quizRating.setQuizId(rs.getLong("quiz_id"));
                quizRating.setUserId(rs.getLong("user_id"));
                quizRating.setRating(rs.getInt("rating"));
                quizRating.setReview(rs.getString("review"));
                quizRatings.add(quizRating);
            }
            return quizRatings;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
