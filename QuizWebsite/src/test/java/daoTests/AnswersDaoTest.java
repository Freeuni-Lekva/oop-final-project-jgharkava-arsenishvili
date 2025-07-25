package daoTests;

import org.ja.dao.*;
import org.ja.model.data.Answer;
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


/**
 * Unit tests for the AnswersDao class using an in-memory H2 database.
 */
public class AnswersDaoTest extends BaseDaoTest{
    private AnswersDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new AnswersDao(basicDataSource);
    }


    @Test
    public void testGetQuestionAnswers() {
        List<Answer> answers = dao.getQuestionAnswers(3);
        assertEquals(3, answers.size());

        assertEquals("CODA", answers.get(0).getAnswerText());
        assertTrue(answers.get(0).getAnswerValidity());
        assertEquals("Dune", answers.get(1).getAnswerText());
        assertFalse(answers.get(1).getAnswerValidity());
    }


    @Test
    public void testInsertAnswer() {
        Answer newAnswer = new Answer(0L, 1L, "Eleven", 2, false);
        assertTrue(dao.insertAnswer(newAnswer));
        assertTrue(newAnswer.getAnswerId() > 0);

        List<Answer> updated = dao.getQuestionAnswers(1);
        assertEquals(2, updated.size()); // originally had 1 correct answer
    }


    @Test
    public void testRemoveAnswer() {
        List<Answer> before = dao.getQuestionAnswers(6); // e.g., contains HTML
        assertTrue(before.stream().anyMatch(a -> a.getAnswerText().equals("HTML")));

        Answer htmlAnswer = before.stream().filter(a -> a.getAnswerText().equals("HTML")).findFirst().get();
        assertTrue(dao.removeAnswer(htmlAnswer.getAnswerId()));

        List<Answer> after = dao.getQuestionAnswers(6);
        assertFalse(after.stream().anyMatch(a -> a.getAnswerText().equals("HTML")));

        assertFalse(dao.removeAnswer(htmlAnswer.getAnswerId())); // already removed
    }


    @Test
    public void testInsertNewAnswerOption() {
        Answer apple = dao.getQuestionAnswers(5).stream()
                .filter(a -> a.getAnswerText().equals("Apple")).findFirst().get();

        assertTrue(dao.insertNewAnswerOption(apple.getAnswerId(), "IBM"));

        List<Answer> updated = dao.getQuestionAnswers(5);
        String newText = updated.stream().filter(a -> a.getAnswerId() == apple.getAnswerId()).findFirst().get().getAnswerText();

        assertEquals("Apple¶IBM", newText);
    }



    @Test
    public void testUpdateAnswerOptionText() {
        Answer answer = dao.getQuestionAnswers(5).stream()
                .filter(a -> a.getAnswerText().contains("Apple")).findFirst().get();

        long id = answer.getAnswerId();
        dao.insertNewAnswerOption(id, "Dell");

        assertTrue(dao.updateAnswerOptionText(id, "Dell", "HP"));

        String text = dao.getQuestionAnswers(5).stream()
                .filter(a -> a.getAnswerId() == id).findFirst().get().getAnswerText();

        assertTrue(text.contains("HP"));
        assertFalse(text.contains("Dell"));
    }


    @Test
    public void testRemoveAnswerOption() {
        Answer answer = dao.getQuestionAnswers(5).stream()
                .filter(a -> a.getAnswerText().contains("Microsoft")).findFirst().get();

        long id = answer.getAnswerId();
        dao.insertNewAnswerOption(id, "Sun");

        assertTrue(dao.removeAnswerOption(id, "Sun"));
        String text = dao.getQuestionAnswers(5).stream()
                .filter(a -> a.getAnswerId() == id).findFirst().get().getAnswerText();

        assertFalse(text.contains("Sun"));
    }


    @Test
    public void testSetOneCorrectChoice() {
        long questionId = 3L;

        List<Answer> answers = dao.getQuestionAnswers(questionId);

        long correctChoiceId = -1;
        for (Answer a : answers) {
            if ("Dune".equalsIgnoreCase(a.getAnswerText())) {
                correctChoiceId = a.getAnswerId();
                break;
            }
        }

        assertTrue(correctChoiceId != -1, "Answer 'Dune' must exist");

        boolean updated = dao.setOneCorrectChoice(questionId, correctChoiceId);
        assertTrue(updated, "setOneCorrectChoice should return true");

        List<Answer> updatedAnswers = dao.getQuestionAnswers(questionId);
        for (Answer ans : updatedAnswers) {
            if (ans.getAnswerId() == correctChoiceId) {
                assertTrue(ans.getAnswerValidity(), "The chosen answer should be marked correct");
            } else {
                assertFalse(ans.getAnswerValidity(), "Other answers should be marked incorrect");
            }
        }
    }


    @Test
    public void testSetChoiceValidity() {
        List<Answer> answers = dao.getQuestionAnswers(6);
        long photoshopId = answers.stream().filter(a -> a.getAnswerText().equals("Photoshop")).findFirst().get().getAnswerId();

        assertTrue(dao.setChoiceValidity(photoshopId, true));
        Answer updated = dao.getQuestionAnswers(6).stream().filter(a -> a.getAnswerId() == photoshopId).findFirst().get();
        assertTrue(updated.getAnswerValidity());
    }


    @Test
    public void testUpdateAnswerText() {
        Answer answer = dao.getQuestionAnswers(1).get(0);
        long id = answer.getAnswerId();

        assertTrue(dao.updateAnswer(id, "Thirteen"));
        String updatedText = dao.getQuestionAnswers(1).get(0).getAnswerText();
        assertEquals("Thirteen", updatedText);
    }


    @Test
    public void testInsertRemoveCorrectAnswerUpdatesCounts() {
        long questionId = 7L; // matching question
        long quizId = 6L;

        int oldNumAnswers = getQuestionNumAnswers(questionId);
        int oldQuizScore = getQuizScore(quizId);

        Answer correct = new Answer(0L, questionId, "Correct Answer", 3, true);

        assertTrue(dao.insertAnswer(correct));
        assertTrue(correct.getAnswerId() > 0);

        assertEquals(oldNumAnswers + 1, getQuestionNumAnswers(questionId));
        assertEquals(oldQuizScore + 1, getQuizScore(quizId));

        assertTrue(dao.removeAnswer(correct.getAnswerId()));

        assertEquals(oldNumAnswers, getQuestionNumAnswers(questionId));
        assertEquals(oldQuizScore, getQuizScore(quizId));
    }


    @Test
    public void testSetChoiceValidityAffectsCounts() {
        long questionId = 6L;
        long quizId = 6L;

        Answer answer = dao.getQuestionAnswers(questionId).stream()
                .filter(a -> !a.getAnswerValidity()).findFirst().get();

        int oldNumAnswers = getQuestionNumAnswers(questionId);
        int oldQuizScore = getQuizScore(quizId);

        assertTrue(dao.setChoiceValidity(answer.getAnswerId(), true));

        assertEquals(oldNumAnswers + 1, getQuestionNumAnswers(questionId));
        assertEquals(oldQuizScore + 1, getQuizScore(quizId));
    }


    @Test
    public void testUnsettingCorrectAnswerUpdatesCounts() {
        long questionId = 6L; // belongs to quizId 1
        long quizId = 6L;

        Answer correct = dao.getQuestionAnswers(questionId).stream()
                .filter(Answer::getAnswerValidity).findFirst().get();

        int oldNumAnswers = getQuestionNumAnswers(questionId);
        int oldQuizScore = getQuizScore(quizId);

        assertTrue(dao.setChoiceValidity(correct.getAnswerId(), false));

        assertEquals(oldNumAnswers - 1, getQuestionNumAnswers(questionId));
        assertEquals(oldQuizScore - 1, getQuizScore(quizId));
    }


    // --- Mockito Tests ---


    @Test
    public void insertAnswer_throwsWhenNoGeneratedId() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        Answer answer = new Answer();
        answer.setQuestionId(1);
        answer.setAnswerText("Test");
        answer.setAnswerOrder(1);
        answer.setAnswerValidity(true);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(mockPs);
        when(mockPs.executeUpdate()).thenReturn(1);
        when(mockPs.getGeneratedKeys()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);  // No ID generated

        AnswersDao dao = new AnswersDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAnswer(answer));
        assertTrue(ex.getMessage().contains("no ID was returned"));
    }


    @Test
    public void insertAnswer_throwsOnSQLException() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        when(mockDs.getConnection()).thenThrow(new SQLException("DB down"));

        AnswersDao dao = new AnswersDao(mockDs);

        Answer answer = new Answer();
        answer.setQuestionId(1);
        answer.setAnswerText("Test");
        answer.setAnswerOrder(1);
        answer.setAnswerValidity(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.insertAnswer(answer));
        assertTrue(ex.getMessage().contains("Error inserting answer"));
    }


    @Test
    public void getQuestionAnswers_throwsOnSQLException() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(anyString())).thenThrow(new SQLException("Select fail"));

        AnswersDao dao = new AnswersDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> dao.getQuestionAnswers(1L));
        assertTrue(ex.getMessage().contains("Error querying answers"));
    }


    @Test
    public void insertNewAnswerOption_throwsWhenAnswerNotFound() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockSelect = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(startsWith("SELECT"))).thenReturn(mockSelect);
        when(mockSelect.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);  // simulate no answer found

        AnswersDao dao = new AnswersDao(mockDs);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dao.insertNewAnswerOption(123L, "new option"));
        assertTrue(ex.getMessage().contains("does not exist"));
    }


    @Test
    public void insertNewAnswerOption_throwsOnSQLException() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        when(mockDs.getConnection()).thenThrow(new SQLException("DB error"));

        AnswersDao dao = new AnswersDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dao.insertNewAnswerOption(1L, "text"));
        assertTrue(ex.getMessage().contains("Database error"));
    }


    @Test
    public void updateAnswerOptionText_throwsWhenOldTextNotFound() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockSelect = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(startsWith("SELECT"))).thenReturn(mockSelect);
        when(mockSelect.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString(1)).thenReturn("option1¶option2");

        AnswersDao dao = new AnswersDao(mockDs);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dao.updateAnswerOptionText(1L, "missing old text", "new text"));
        assertTrue(ex.getMessage().contains("not found"));
    }


    @Test
    public void updateAnswerOptionText_throwsWhenAnswerNotFound() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockSelect = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(startsWith("SELECT"))).thenReturn(mockSelect);
        when(mockSelect.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(false);  // no answer found

        AnswersDao dao = new AnswersDao(mockDs);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> dao.updateAnswerOptionText(1L, "old", "new"));
        assertTrue(ex.getMessage().contains("does not exist"));
    }


    @Test
    public void updateAnswerOptionText_throwsOnSQLException() throws Exception {
        BasicDataSource mockDs = mock(BasicDataSource.class);
        Connection mockConn = mock(Connection.class);
        PreparedStatement mockSelect = mock(PreparedStatement.class);
        ResultSet mockRs = mock(ResultSet.class);
        PreparedStatement mockUpdate = mock(PreparedStatement.class);

        when(mockDs.getConnection()).thenReturn(mockConn);
        when(mockConn.prepareStatement(startsWith("SELECT"))).thenReturn(mockSelect);
        when(mockSelect.executeQuery()).thenReturn(mockRs);
        when(mockRs.next()).thenReturn(true);
        when(mockRs.getString(1)).thenReturn("oldtext");
        when(mockConn.prepareStatement(startsWith("UPDATE"))).thenReturn(mockUpdate);
        when(mockUpdate.executeUpdate()).thenThrow(new SQLException("Update fail"));

        AnswersDao dao = new AnswersDao(mockDs);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> dao.updateAnswerOptionText(1L, "oldtext", "newtext"));
        assertTrue(ex.getMessage().contains("Error updating answer option text"));
    }

}
