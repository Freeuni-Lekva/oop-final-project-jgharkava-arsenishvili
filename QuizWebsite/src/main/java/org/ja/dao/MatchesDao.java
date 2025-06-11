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

    public MatchesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void insertMatch(Match match) {
        String sql = "insert into matches values(?,?,?,?)";
        try(Connection c=dataSource.getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){

            ps.setLong(1, match.getMatchId());
            ps.setLong(2, match.getQuestionId());
            ps.setString(3, match.getLeftMatch());
            ps.setString(4, match.getRightMatch());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting Match to database", e);
        }
    }

    public void removeMatch(long matchId) {
        String sql = "delete from matches where match_id=?";
        try (Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, matchId);

            ps.executeUpdate();
        } catch(SQLException e){
            throw new RuntimeException("Error removing Match from database", e);
        }
    }

    public ArrayList<Match>getQuestionMatches(long questionId) {
        ArrayList<Match> matches = new ArrayList<>();

        String sql="select * from matches where question_id=?";
        try(Connection c = dataSource.getConnection();
            PreparedStatement ps = c.prepareStatement(sql)){

            ps.setLong(1, questionId);

            ResultSet rs = ps.executeQuery();

            while(rs.next())
                matches.add(retrieveMatch(rs));

        } catch(SQLException e){
            throw new RuntimeException("Error querying Matches of a question from database", e);
        }

        return matches;
    }

    private Match retrieveMatch(ResultSet rs) throws SQLException {
        return new Match(rs.getLong("match_id"), rs.getLong("question_id"),
                rs.getString("left_match"), rs.getString("right_match"));
    }
}
