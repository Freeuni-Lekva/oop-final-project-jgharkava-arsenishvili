package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.data.Match;
import org.ja.model.quiz.question.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for managing matches, particular type of question, in the database.
 */
public class MatchesDao extends BaseDao{

    /**
     * Constructs a MatchesDao with the given data source.
     *
     * @param dataSource the database connection pool
     */
    public MatchesDao(BasicDataSource dataSource) {
        super(dataSource);
    }


    /**
     * Inserts a new match pair into the database.
     *
     * @param match the Match object containing question ID and both sides of the match
     * @return true if insertion was successful and match ID was generated, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean insertMatch(Match match) {
        String sql = "INSERT INTO matches (question_id, left_match, right_match) VALUES (?,?,?)";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, match.getQuestionId());
            ps.setString(2, match.getLeftMatch());
            ps.setString(3, match.getRightMatch());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0){
                return false;
            }

            try (ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()) {
                    match.setMatchId(rs.getLong(1));
                } else {
                    throw new RuntimeException("Insert succeeded but no ID was returned.");
                }
            }

            long questionId = match.getQuestionId();

            updateQuestionNumAnswers(questionId);
            updateQuizScore(getQuizId(questionId));

            return true;
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Match to database", e);
        }
    }


    /**
     * Removes a match from the database by its match ID.
     *
     * @param matchId the unique ID of the match to remove
     * @return true if deletion was successful, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean removeMatch(long matchId) {

        // If it does not exist, return false
        Match match = getMatchById(matchId);
        if (match == null) return false;

        Question question = getQuestionById(match.getQuestionId());
        if (question == null) return false;

        String sql = "delete from matches where match_id = ?";

        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, matchId);

            if (ps.executeUpdate() == 0){
                return false;
            }

            updateQuestionNumAnswers(question.getQuestionId());
            updateQuizScore(question.getQuizId());

            return true;
        } catch(SQLException e){
            throw new RuntimeException("Error removing Match from database", e);
        }
    }


    /**
     * Retrieves all matches associated with a given question.
     *
     * @param questionId the ID of the question
     * @return a list of Match objects for that question
     * @throws RuntimeException if a database error occurs
     */
    public List<Match> getQuestionMatches(long questionId) {
        List<Match> matches = new ArrayList<>();

        String sql = "select * from matches where question_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, questionId);

            try (ResultSet rs = ps.executeQuery()){
                while (rs.next())
                    matches.add(retrieveMatch(rs));
            }
        } catch (SQLException e){
            throw new RuntimeException("Error querying Matches of a question from database", e);
        }

        return matches;
    }


    /**
     * Updates the left_match text of a match entry.
     *
     * @param matchId  the ID of the match to update
     * @param newLeft  the new value for the left match
     * @return true if the update was successful, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean updateLeftMatch(long matchId, String newLeft){
        String sql = "UPDATE matches SET left_match = ? WHERE match_id = ?";

        return updateMatch(matchId, newLeft, sql);
    }


    /**
     * Updates the right_match text of a match entry.
     *
     * @param matchId  the ID of the match to update
     * @param newRight  the new value for the left match
     * @return true if the update was successful, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    public boolean updateRightMatch(long matchId, String newRight){
        String sql = "UPDATE matches SET right_match = ? WHERE match_id = ?";

        return updateMatch(matchId, newRight, sql);
    }


    // --- Helper Methods ---


    /**
     * Retrieves a match by its ID.
     *
     * @param matchId the ID of the question
     * @return the corresponding Match object, or null if not found
     */
    private Match getMatchById(long matchId){
        String sql = "SELECT * FROM matches WHERE match_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, matchId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return retrieveMatch(rs);
                } else {
                    return null;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching matcg by ID: " + matchId, e);
        }
    }


    /**
     * Shared helper method for updating either left or right match value.
     *
     * @param matchId the match ID to update
     * @param newValue the new match value
     * @param sql the update SQL string
     * @return true if the update succeeded, false otherwise
     * @throws RuntimeException if a database error occurs
     */
    private boolean updateMatch(long matchId, String newValue, String sql) {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)){

            ps.setString(1, newValue);
            ps.setLong(2, matchId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e){
            throw new RuntimeException("Error updating left match text", e);
        }
    }



    /**
     * Helper method to construct a Match object from a database row.
     *
     * @param rs the ResultSet containing match data
     * @return Match object populated from ResultSet
     * @throws SQLException if reading from the result set fails
     */
    private Match retrieveMatch(ResultSet rs) throws SQLException {
        return new Match(rs.getLong("match_id"), rs.getLong("question_id"),
                rs.getString("left_match"), rs.getString("right_match"));
    }
}
