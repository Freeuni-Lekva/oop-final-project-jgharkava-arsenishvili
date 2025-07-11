package org.ja.dao;


import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.QuizTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;



/**
 * Data Access Object for handling operations related to the {@code quiz_tag} table,
 * which associates quizzes with their respective tags.
 */
public class QuizTagsDao {
    private final BasicDataSource dataSource;

    /**
     * Constructs a QuizTagsDao with the given data source.
     *
     * @param dataSource the database connection pool
     */
    public QuizTagsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * Inserts a new quiz-tag mapping into the {@code quiz_tag} table.
     * <p>
     * If the (quiz_id, tag_id) combination already exists (i.e., primary key conflict),
     * a {@code RuntimeException} is thrown.
     *
     * @param quizTag the {@code QuizTag} object representing the mapping to insert
     * @return {@code true} if the insertion was successful; {@code false} otherwise
     * @throws RuntimeException if a SQL exception occurs during insertion
     */
    public boolean insertQuizTag(QuizTag quizTag) {
        String sql = "INSERT INTO quiz_tag VALUES (?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, quizTag.getQuizId());
            ps.setLong(2, quizTag.getTagId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting quiz tag into database", e);
        }
    }


    /**
     * Removes a quiz-tag mapping from the {@code quiz_tag} table using quiz ID and tag ID.
     *
     * @param qid the ID of the quiz
     * @param tid the ID of the tag
     * @return {@code true} if the mapping was removed; {@code false} if no such mapping exists
     * @throws RuntimeException if a SQL exception occurs during deletion
     */
    public boolean removeQuizTag(long qid, long tid) {
        String sql = "DELETE FROM quiz_tag WHERE quiz_id = ? AND tag_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, qid);
            ps.setLong(2, tid);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error removing quiz tag from database", e);
        }
    }


    /**
     * Retrieves a list of tag IDs associated with a given quiz ID.
     *
     * @param quizId the ID of the quiz
     * @param limit the maximum number of tags to return
     * @return a list of tag IDs for the specified quiz
     * @throws RuntimeException if a SQL exception occurs during retrieval
     */
    public List<Long> getTagsByQuizId(long quizId, int limit) {
        List<Long> tagIds = new ArrayList<>();

        String sql = "select * from quiz_tag where quiz_id = ? LIMIT ?";

        try (Connection c = dataSource.getConnection()){
            PreparedStatement ps = c.prepareStatement(sql);

            ps.setLong(1, quizId);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    tagIds.add(rs.getLong("tag_id"));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error querying quiz tags by quiz id from database", e);
        }

        return tagIds;
    }


    // --- Helper Methods ---


    /**
     * Helper method to construct a {@code QuizTag} object from a {@code ResultSet}.
     *
     * @param rs the result set positioned at a valid row
     * @return a {@code QuizTag} object extracted from the current row
     * @throws SQLException if a database access error occurs
     */
    private QuizTag retrieveQuizTag(ResultSet rs) throws SQLException {
        return new QuizTag(rs.getLong("quiz_id"), rs.getLong("tag_id"));
    }
}
