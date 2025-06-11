package org.ja.dao;

import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.Match;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
    BasicDataSource dataSource;
    public MatchesDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertMatch(Match match) {
        String sql="insert into matches values(?,?,?,?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, match.getMatchId());
            ps.setLong(2, match.getQuestionId());
            ps.setString(3, match.getLeftMatch());
            ps.setString(4, match.getRightMatch());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void removeMatch(long matchId) {
        String sql="delete from matches where match_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, matchId);
            ps.executeUpdate();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public ArrayList<Match>getQuestionMatches(long questionId) {
        String sql="select * from matches where question_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, questionId);
            ResultSet rs=ps.executeQuery();
            ArrayList<Match> ans=new ArrayList<>();
            while(rs.next()){
                Match match=new Match();
                match.setMatchId(rs.getLong("match_id"));
                match.setQuestionId(rs.getLong("question_id"));
                match.setLeftMatch(rs.getString("left_match"));
                match.setRightMatch(rs.getString("right_match"));
                ans.add(match);
            }
            return ans;
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
