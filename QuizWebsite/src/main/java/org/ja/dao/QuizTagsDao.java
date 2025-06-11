package org.ja.dao;


import org.apache.commons.dbcp2.BasicDataSource;
import org.ja.model.OtherObjects.QuizTag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/*
create table quiz_tag(
    quiz_id bigint not null,
    tag_id bigint not null,
 */
public class QuizTagsDao {
    BasicDataSource dataSource;
    public QuizTagsDao(BasicDataSource dataSource) {
        this.dataSource = dataSource;
    }
    public void insertQuizTag(QuizTag quizTag) {
        String sql="insert into quiz_tag values(?,?)";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, quizTag.getQuizId());
            ps.setLong(2, quizTag.getTagId());
            ps.executeUpdate();

        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
    public void removeQuizTag(long qid, long tid) {
        String sql="DELETE FROM quiz_tag WHERE quiz_id=? AND tag_id=?";
        try(Connection c= dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, qid);
            ps.setLong(2, tid);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public ArrayList<QuizTag> getQuizTagsByQuizId(long quizId) {
        String sql="select * from quiz_tag where quiz_id=?";
        try(Connection c=dataSource.getConnection()){
            PreparedStatement ps=c.prepareStatement(sql);
            ps.setLong(1, quizId);
            ResultSet rs=ps.executeQuery();
            ArrayList<QuizTag>ans=new ArrayList<>();
            while(rs.next()){
                QuizTag quizTag=new QuizTag();
                quizTag.setQuizId(rs.getLong("quiz_id"));
                quizTag.setTagId(rs.getLong("tag_id"));
                ans.add(quizTag);
            }
            return ans;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
