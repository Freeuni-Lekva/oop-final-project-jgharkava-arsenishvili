package daoTests;

import org.ja.dao.*;
import org.ja.model.quiz.question.MatchingQuestion;
import org.ja.model.quiz.question.MultiAnswerQuestion;
import org.ja.model.quiz.question.Question;
import org.ja.model.quiz.question.ResponseQuestion;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


public class QuestionDaoTest extends BaseDaoTest{

    private QuestionDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new QuestionDao(basicDataSource);
    }

    @Test
    public void testInsertAndRemoveQuestion(){
        Question q = new Question(-1L, 4L, "What is 3 + 4?", null,
                Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        boolean inserted = dao.insertQuestion(q);
        assertTrue(inserted);
        assertTrue(q.getQuestionId() > 0);

        List<Question> questions = dao.getQuizQuestions(4);
        assertTrue(questions.stream().anyMatch(q2 -> q2.getQuestionId() == q.getQuestionId()));

        assertTrue(dao.removeQuestion(q.getQuestionId()));
        assertFalse(dao.removeQuestion(q.getQuestionId())); // already deleted
    }


    @Test
    public void testUpdateQuestionText() {
        Question q = new ResponseQuestion(-1L, 4L, "Old Text", null,
                Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        assertTrue(dao.insertQuestion(q));

        String newText = "Updated Question Text";
        assertTrue(dao.updateQuestionText(q.getQuestionId(), newText));

        List<Question> updated = dao.getQuizQuestions(4L);
        assertTrue(updated.stream()
                .anyMatch(q2 -> q2.getQuestionId() == q.getQuestionId() && q2.getQuestionText().equals(newText)));
    }


    @Test
    public void testUpdateQuestionImage() {
        Question q = new ResponseQuestion(-1L, 4L, "With image", null,
                Constants.QuestionTypes.RESPONSE_QUESTION, 1, Constants.OrderTypes.ORDERED);

        assertTrue(dao.insertQuestion(q));

        String imageUrl = "https://example.com/image.png";
        assertTrue(dao.updateQuestionImage(q.getQuestionId(), imageUrl));

        List<Question> updated = dao.getQuizQuestions(4);
        assertTrue(updated.stream()
                .anyMatch(q2 -> q2.getQuestionId() == q.getQuestionId() && q2.getImageUrl().equals(imageUrl)));
    }


    @Test
    public void testQuizScoreUpdateAfterRemoval() {
        Question q = new MultiAnswerQuestion(-1L, 4L, "Quiz score test", null,
                Constants.QuestionTypes.MULTI_ANSWER_QUESTION, 3, Constants.OrderTypes.ORDERED);

        assertTrue(dao.insertQuestion(q));

        long quizId = 4L;
        int scoreBefore = getQuizScore(quizId);

        assertTrue(dao.removeQuestion(q.getQuestionId()));

        int scoreAfter = getQuizScore(quizId);
        assertEquals(scoreBefore - 3, scoreAfter);
    }


    @Test
    public void testInsertQuestionUpdatesQuizScore() {
        long quizId = 4;
        int scoreBefore = getQuizScore(quizId);

        Question q = new MatchingQuestion(-1L, quizId, "New math question", null,
                Constants.QuestionTypes.MATCHING_QUESTION, 2, Constants.OrderTypes.ORDERED);

        boolean inserted = dao.insertQuestion(q);
        assertTrue(inserted);
        assertTrue(q.getQuestionId() > 0);

        int scoreAfter = getQuizScore(quizId);
        assertEquals(scoreBefore + 2, scoreAfter);

        assertTrue(dao.removeQuestion(q.getQuestionId()));
        assertEquals(scoreBefore, getQuizScore(quizId));
    }


    @Test
    public void testRemoveQuestionUpdatesQuizScore() {
        long quizId = 4;

        dao.removeQuestion(1);
        assertEquals(1, getQuizScore(quizId));

        dao.removeQuestion(2);
        assertEquals(0, getQuizScore(quizId));
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertQuestion_throwsExceptionOnInsert() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        QuestionDao dao = new QuestionDao(ds);
        Question q = new Question();
        q.setQuizId(1L);

        assertThrows(RuntimeException.class, () -> dao.insertQuestion(q));
    }


    @Test
    public void testInsertQuestion_throwsExceptionOnGetGeneratedKeys() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);
        when(ps.getGeneratedKeys()).thenReturn(rs);
        when(rs.next()).thenReturn(false); // no ID returned

        QuestionDao dao = new QuestionDao(ds);
        Question q = new Question();
        q.setQuizId(1L);

        assertThrows(RuntimeException.class, () -> dao.insertQuestion(q));
    }


    @Test
    public void testGetQuizQuestions_throwsExceptionOnQuery() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuestionDao dao = new QuestionDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizQuestions(1L));
    }


    @Test
    public void testUpdateQuestionText_throwsExceptionOnUpdate() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuestionDao dao = new QuestionDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuestionText(1L, "New text"));
    }


    @Test
    public void testUpdateQuestionImage_throwsExceptionOnUpdate() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuestionDao dao = new QuestionDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuestionImage(1L, "new-image-url"));
    }
}
