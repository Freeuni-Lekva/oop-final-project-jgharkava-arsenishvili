package org.ja.dao;


import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.QuizTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/*
create table quiz_tag(
    quiz_id bigint not null,
    tag_id bigint not null,
 */
public class QuizTagsDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public QuizTagsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    /// if already exists row with same quizId and tagId throws RuntimeException
    public void insertQuizTag(QuizTag quizTag) {
        String sql = "insert into quiz_tag values (?,?)";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizTag.getQuizId());
            ps.setLong(2, quizTag.getTagId());

            ps.executeUpdate();
            cnt++;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz tag into database", e);
        }
    }

    public void removeQuizTag(long qid, long tid) {
        String sql = "DELETE FROM quiz_tag WHERE quiz_id = ? AND tag_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, qid);
            ps.setLong(2, tid);

            if(ps.executeUpdate() > 0)
                cnt--;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz tag from database", e);
        }
    }

    public ArrayList<QuizTag> getQuizTagsByQuizId(long quizId) {
        ArrayList<QuizTag> quizTags = new ArrayList<>();

        String sql = "select * from quiz_tag where quiz_id = ?";
        try (Connection c = dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);

            ps.setLong(1, quizId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    quizTags.add(retrieveQuizTag(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz tags by quiz id from database", e);
        }

        return quizTags;
    }

    public boolean contains(QuizTag qt){
        if(qt==null){
            return false;
        }
        String sql = "SELECT COUNT(*) FROM quiz_tag WHERE quiz_id = ? AND tag_id=?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, qt.getQuizId());
            ps.setLong(2, qt.getTagId());

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
    private QuizTag retrieveQuizTag(ResultSet rs) throws SQLException {
        return new QuizTag(rs.getLong("quiz_id"), rs.getLong("tag_id"));
    }
}
