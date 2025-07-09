package DaoTests;

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


}
