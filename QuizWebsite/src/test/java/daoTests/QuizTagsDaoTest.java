package daoTests;

import org.ja.dao.*;
import org.ja.utils.Constants;
import org.ja.model.data.QuizTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for the QuizTagsDao class using an in-memory H2 database.
 */
public class QuizTagsDaoTest extends BaseDaoTest{

    private QuizTagsDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new QuizTagsDao(basicDataSource);
    }


    @Test
    public void testInsertQuizTag() {
        QuizTag tag = new QuizTag(4L, 6L);
        assertTrue(dao.insertQuizTag(tag));

        List<Long> tagIds = dao.getTagsByQuizId(4L, Constants.FETCH_LIMIT);
        assertTrue(tagIds.contains(6L));
    }


    @Test
    public void testInsertDuplicateQuizTagThrowsException() {
        QuizTag tag = new QuizTag(4L, 4); // Already exists in test DB setup

        assertThrows(RuntimeException.class, () -> dao.insertQuizTag(tag));
    }


    @Test
    public void testRemoveQuizTag() {
        QuizTag tag = new QuizTag(4L, 6L);
        assertTrue(dao.insertQuizTag(tag));

        assertTrue(dao.removeQuizTag(4L, 6L));

        List<Long> tagIds = dao.getTagsByQuizId(4L, Constants.FETCH_LIMIT);
        assertFalse(tagIds.contains(6L));
        assertFalse(dao.removeQuizTag(1225L, 1225)); // Not present
    }


    @Test
    public void testGetTagsByQuizId() {
        List<Long> tagIds = dao.getTagsByQuizId(6L, Constants.FETCH_LIMIT);
        assertNotNull(tagIds);
        assertEquals(2, tagIds.size()); // Based on sample dataset
        assertTrue(tagIds.contains(6L));
        assertTrue(tagIds.contains(4L));
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertQuizTag_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        QuizTagsDao dao = new QuizTagsDao(ds);
        QuizTag quizTag = new QuizTag(1L, 2L);

        assertThrows(RuntimeException.class, () -> dao.insertQuizTag(quizTag));
    }


    @Test
    public void testRemoveQuizTag_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Delete failed"));

        QuizTagsDao dao = new QuizTagsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.removeQuizTag(1L, 2L));
    }


    @Test
    public void testGetTagsByQuizId_throwsExceptionOnPrepareStatement() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenThrow(new SQLException("Prepare failed"));

        QuizTagsDao dao = new QuizTagsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTagsByQuizId(1L, 10));
    }


    @Test
    public void testGetTagsByQuizId_throwsExceptionOnExecuteQuery() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizTagsDao dao = new QuizTagsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTagsByQuizId(1L, 10));
    }
}
