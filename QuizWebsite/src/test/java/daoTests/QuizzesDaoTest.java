package daoTests;

import org.ja.dao.QuizzesDao;
import org.ja.model.filters.Filter;
import org.ja.model.quiz.Quiz;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
 * Unit tests for the QuizzesDao class using an in-memory H2 database.
 */
public class QuizzesDaoTest extends BaseDaoTest{
    private QuizzesDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new QuizzesDao(basicDataSource);
    }


    @Test
    public void testGetQuizById() {
        long existingQuizId = 4;
        Quiz quizExisting = dao.getQuizById(existingQuizId);

        assertNotNull(quizExisting);

        assertEquals("Basic Algebra", quizExisting.getName());
        assertEquals(6, quizExisting.getCreatorId());

        long nonExistingId = 1225;
        Quiz quizNonExisting = dao.getQuizById(nonExistingId);
        assertNull(quizNonExisting);
    }


    @Test
    public void testGetQuizByName() {
        Quiz quiz = dao.getQuizByName("Basic Algebra");
        assertNotNull(quiz);
        assertEquals(4, quiz.getId());
    }


    @Test
    public void testGetQuizzesByCreatorId() {
        List<Quiz> quizzes = dao.getQuizzesByCreatorId(5, Constants.FETCH_LIMIT); // Mariam
        assertEquals(1, quizzes.size());
        assertEquals("Oscar Winners", quizzes.get(0).getName());
    }


    @Test
    public void testGetQuizzesSortedByCreationDate() {
        List<Quiz> quizzes = dao.getQuizzesSortedByCreationDate(Constants.FETCH_LIMIT);
        assertFalse(quizzes.isEmpty());

        // non-increasing
        for (int i = 1; i < quizzes.size(); i++) {
            assertFalse(quizzes.get(i).getCreationDate().after(quizzes.get(i - 1).getCreationDate()),
                    "Quizzes are not sorted by creation date descending");
        }
    }


    @Test
    public void testGetFriendsQuizzesSortedByCreationDate() {
        List<Quiz> quizzes = dao.getFriendsQuizzesSortedByCreationDate(6, Constants.FETCH_LIMIT); // Gio is friends with 5 (Mariam)
        assertFalse(quizzes.isEmpty());
        assertTrue(quizzes.stream().anyMatch(q -> q.getCreatorId() == 5));
    }


    @Test
    public void testInsertQuiz() {
        Quiz newQuiz = new Quiz(-1L, "Test Insert", "Testing insert method", 5, 0, 0,
                Timestamp.valueOf(LocalDateTime.now()), 15, 5, 5,
                Constants.QuizQuestionOrderTypes.QUESTIONS_ORDERED,
                Constants.QuizQuestionPlacementTypes.ONE_PAGE,
                Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION);

        boolean inserted = dao.insertQuiz(newQuiz);
        assertTrue(inserted);
        assertTrue(newQuiz.getId() > 0);

        Quiz fetched = dao.getQuizById(newQuiz.getId());
        assertNotNull(fetched);
        assertEquals("Test Insert", fetched.getName());
    }


    @Test
    public void testUpdateQuizTitle() {
        long quizId = 4;
        String newTitle = "Updated Algebra Quiz";
        boolean updated = dao.updateQuizTitle(quizId, newTitle);
        assertTrue(updated);

        Quiz quiz = dao.getQuizById(quizId);
        assertEquals(newTitle, quiz.getName());

        boolean updatedNonExistent = dao.updateQuizTitle(1225, "No quiz");
        assertFalse(updatedNonExistent);
    }


    @Test
    public void testUpdateQuizDescription() {
        boolean result = dao.updateQuizDescription(4, "New description");
        assertTrue(result);
        assertEquals("New description", dao.getQuizById(4).getDescription());
    }


    @Test
    public void testUpdateQuizTimeLimit() {
        boolean result = dao.updateQuizTimeLimit(4, 15);
        assertTrue(result);
        assertEquals(15, dao.getQuizById(4).getTimeInMinutes());
    }


    @Test
    public void testUpdateQuizCategory() {
        boolean result = dao.updateQuizCategory(4, 8); // 'Art'
        assertTrue(result);
        assertEquals(8, dao.getQuizById(4).getCategoryId());
    }


    @Test
    public void testRemoveQuizById() {
        long quizId = 6;
        boolean removed = dao.removeQuizById(quizId);
        assertTrue(removed);

        Quiz quiz = dao.getQuizById(quizId);
        assertNull(quiz);
    }


    @Test
    public void testFilterQuizzes() {
        Filter dummyFilter = new Filter() {
            @Override
            public List<Object> getParameters() {
                return List.of("%r%", "Movies", "Art", "timed");
            }

            @Override
            public String buildWhereClause() {
                return "quiz_name like ? and (category_name = ? or category_name = ? or tag_name = ?)";
            }

            @Override
            public String buildOrderByClause() {
                return "time_limit_in_minutes desc";
            }
        };

        List<Quiz> quizzes = dao.filterQuizzes(dummyFilter, Constants.FETCH_LIMIT);

        assertEquals( 2, quizzes.size());
        assertEquals("Oscar Winners", quizzes.get(1).getName());
        assertEquals("Basic Algebra", quizzes.get(0).getName());
    }


    @Test
    public void testGetQuizzesSortedByParticipantCount() {
        List<Quiz> quizzes = dao.getQuizzesSortedByParticipantCount(Constants.FETCH_LIMIT);
        assertFalse(quizzes.isEmpty());
        for (int i = 0; i < quizzes.size() - 1; i++) {
            assertTrue(quizzes.get(i).getParticipantCount() >= quizzes.get(i + 1).getParticipantCount());
        }
    }


    @Test
    public void testUpdateQuizQuestionOrderStatus() {
        boolean result = dao.updateQuizQuestionOrderStatus(4, Constants.QuizQuestionOrderTypes.QUESTIONS_ORDERED);
        assertTrue(result);
        assertEquals(Constants.QuizQuestionOrderTypes.QUESTIONS_ORDERED, dao.getQuizById(4).getQuestionOrder());
    }


    @Test
    public void testUpdateQuizQuestionPlacementStatus() {
        boolean result = dao.updateQuizQuestionPlacementStatus(4, Constants.QuizQuestionPlacementTypes.ONE_PAGE);
        assertTrue(result);
        assertEquals(Constants.QuizQuestionPlacementTypes.ONE_PAGE, dao.getQuizById(4).getQuestionPlacement());
    }


    @Test
    public void testUpdateQuizQuestionCorrectionStatus() {
        boolean result = dao.updateQuizQuestionCorrectionStatus(4, Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION);
        assertTrue(result);
        assertEquals(Constants.QuizQuestionCorrectionTypes.FINAL_CORRECTION, dao.getQuizById(4).getQuestionCorrection());
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertQuiz_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        QuizzesDao dao = new QuizzesDao(ds);
        Quiz quiz = mock(Quiz.class);
        when(quiz.getName()).thenReturn("name");
        when(quiz.getDescription()).thenReturn("desc");
        when(quiz.getScore()).thenReturn(0);
        when(quiz.getAvgRating()).thenReturn(0.0);
        when(quiz.getParticipantCount()).thenReturn(0L);
        when(quiz.getTimeInMinutes()).thenReturn(0);
        when(quiz.getCategoryId()).thenReturn(0L);
        when(quiz.getCreatorId()).thenReturn(0L);
        when(quiz.getQuestionOrder()).thenReturn("");
        when(quiz.getQuestionPlacement()).thenReturn("");
        when(quiz.getQuestionCorrection()).thenReturn("");

        assertThrows(RuntimeException.class, () -> dao.insertQuiz(quiz));
    }


    @Test
    public void testUpdateQuizTitle_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizTitle(1L, "new title"));
    }


    @Test
    public void testUpdateQuizDescription_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizDescription(1L, "new desc"));
    }


    @Test
    public void testUpdateQuizTimeLimit_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizTimeLimit(1L, 15));
    }


    @Test
    public void testUpdateQuizCategory_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizCategory(1L, 2L));
    }


    @Test
    public void testUpdateQuizQuestionOrderStatus_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizQuestionOrderStatus(1L, "order"));
    }


    @Test
    public void testUpdateQuizQuestionPlacementStatus_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizQuestionPlacementStatus(1L, "placement"));
    }


    @Test
    public void testUpdateQuizQuestionCorrectionStatus_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Update failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.updateQuizQuestionCorrectionStatus(1L, "correction"));
    }


    @Test
    public void testRemoveQuizById_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Delete failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.removeQuizById(1L));
    }


    @Test
    public void testFilterQuizzes_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        Filter filter = mock(Filter.class);
        when(filter.buildWhereClause()).thenReturn("1=1");
        when(filter.buildOrderByClause()).thenReturn("quiz_id");
        when(filter.getParameters()).thenReturn(java.util.Collections.emptyList());

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.filterQuizzes(filter, 10));
    }


    @Test
    public void testGetQuizzesSortedByCreationDate_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizzesSortedByCreationDate(5));
    }


    @Test
    public void testGetFriendsQuizzesSortedByCreationDate_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getFriendsQuizzesSortedByCreationDate(1L, 5));
    }


    @Test
    public void testGetQuizzesSortedByParticipantCount_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizzesSortedByParticipantCount(5));
    }


    @Test
    public void testGetQuizById_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizById(1L));
    }


    @Test
    public void testGetQuizByName_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizByName("name"));
    }


    @Test
    public void testGetQuizzesByCreatorId_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        QuizzesDao dao = new QuizzesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getQuizzesByCreatorId(1L, 10));
    }
}

