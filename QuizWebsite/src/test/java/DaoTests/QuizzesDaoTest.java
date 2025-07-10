package DaoTests;

import org.ja.dao.QuizzesDao;
import org.ja.model.Filters.Filter;
import org.ja.model.quiz.Quiz;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
        List<Quiz> quizzes = dao.getQuizzesByCreatorId(5); // Mariam
        assertEquals(1, quizzes.size());
        assertEquals("Oscar Winners", quizzes.get(0).getName());
    }

    @Test
    public void testGetQuizzesSortedByCreationDate() {
        List<Quiz> quizzes = dao.getQuizzesSortedByCreationDate();
        assertFalse(quizzes.isEmpty());

        // non-increasing
        for (int i = 1; i < quizzes.size(); i++) {
            assertFalse(quizzes.get(i).getCreationDate().after(quizzes.get(i - 1).getCreationDate()),
                    "Quizzes are not sorted by creation date descending");
        }
    }

    @Test
    public void testGetFriendsQuizzesSortedByCreationDate() {
        List<Quiz> quizzes = dao.getFriendsQuizzesSortedByCreationDate(6); // Gio is friends with 5 (Mariam)
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

        List<Quiz> quizzes = dao.filterQuizzes(dummyFilter);

        assertEquals( 2, quizzes.size());
        assertEquals("Oscar Winners", quizzes.get(1).getName());
        assertEquals("Basic Algebra", quizzes.get(0).getName());
    }

    @Test
    public void testGetQuizzesSortedByParticipantCount() {
        List<Quiz> quizzes = dao.getQuizzesSortedByParticipantCount();
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

}

