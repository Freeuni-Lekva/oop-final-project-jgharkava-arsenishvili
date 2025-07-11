package DaoTests;

import org.ja.dao.*;
import org.ja.model.data.QuizTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

        List<Long> tagIds = dao.getTagsByQuizId(4L);
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

        List<Long> tagIds = dao.getTagsByQuizId(4L);
        assertFalse(tagIds.contains(6L));
        assertFalse(dao.removeQuizTag(1225L, 1225)); // Not present
    }


    @Test
    public void testGetTagsByQuizId() {
        List<Long> tagIds = dao.getTagsByQuizId(6L);
        assertNotNull(tagIds);
        assertEquals(2, tagIds.size()); // Based on sample dataset
        assertTrue(tagIds.contains(6L));
        assertTrue(tagIds.contains(4L));
    }

}
