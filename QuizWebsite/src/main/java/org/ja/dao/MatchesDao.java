package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Match;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/*
create table matches(
    match_id bigint primary key auto_increment,
    question_id bigint not null,
    left_match text not null,
    right_match text not null,

    foreign key (question_id) references questions(question_id) on delete cascade
);

 */
public class MatchesDao {
    private final BasicDataSource dataSource;
    private long cnt=0;
    public MatchesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertMatch(Match match) {
        if(contains(match)) {
            return;
        }
        String sql = "insert into matches (question_id, left_match, right_match) values(?,?,?)";
        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, match.getQuestionId());
            ps.setString(2, match.getLeftMatch());
            ps.setString(3, match.getRightMatch());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()){
                if(rs.next()) {
                    cnt++;
                    match.setMatchId(rs.getLong("match_id"));
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Match to database", e);
        }
    }

    public void removeMatch(long matchId) {
        if(getMatchById(matchId) == null) {
            return;
        }
        String sql = "delete from matches where match_id = ?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, matchId);

            ps.executeUpdate();
            cnt--;
        } catch(SQLException e){
            throw new RuntimeException("Error removing Match from database", e);
        }
    }

    public ArrayList<Match>getQuestionMatches(long questionId) {
        ArrayList<Match> matches = new ArrayList<>();

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
    public boolean contains(Match match) {
        String sql = "SELECT COUNT(*) FROM matches WHERE match_id = ? AND question_id=?" +
                "AND left_match=? AND right_match = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, match.getMatchId());
            ps.setLong(2,match.getQuestionId());
            ps.setString(3, match.getLeftMatch());
            ps.setString(4, match.getRightMatch());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user existence", e);
        }
    }
    public Match getMatchById(long matchId) {
        String sql = "select * from matches where match_id = "+matchId;
        try(Connection c=dataSource.getConnection(); PreparedStatement ps=c.prepareStatement(sql)){
            ResultSet rs=ps.executeQuery();
            if(!rs.next()) {
                return null;
            }
            return retrieveMatch(rs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public long getCount(){
        return cnt;
    }
    private Match retrieveMatch(ResultSet rs) throws SQLException {
        return new Match(rs.getLong("match_id"), rs.getLong("question_id"),
                rs.getString("left_match"), rs.getString("right_match"));
    }
}
