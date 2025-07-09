package DaoTests;

import org.ja.dao.TagsDao;
import org.ja.model.CategoriesAndTags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Unit tests for the TagsDao class using an in-memory H2 database.
 */
public class TagsDaoTest extends BaseDaoTest{
    private TagsDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new TagsDao(basicDataSource);
    }

    @Test
    void testInsertTagSuccess() {
        Tag tag = new Tag();
        tag.setTagName("unit-test-tag");
        boolean inserted = dao.insertTag(tag);

        assertTrue(inserted);
        assertTrue(tag.getTagId() > 0);

        Tag retrieved = dao.getTagById(tag.getTagId());
        assertNotNull(retrieved);
        assertEquals("unit-test-tag", retrieved.getTagName());
    }

    @Test
    void testInsertTagDuplicate() {
        Tag tag1 = new Tag();
        tag1.setTagName("duplicate-tag");
        assertTrue(dao.insertTag(tag1));

        Tag tag2 = new Tag();
        tag2.setTagName("duplicate-tag");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            dao.insertTag(tag2);
        });

        assertTrue(exception.getMessage().contains("Error inserting tag"));
    }

    @Test
    public void testGetTagByName(){
        Tag tag = dao.getTagByName("timed");
        assertNotNull(tag);
        assertEquals(4, tag.getTagId());

        Tag tagNonExisting = dao.getTagByName("Goofy");
        assertNull(tagNonExisting);
    }


    @Test
    void testGetTagByIdNotFound() {
        Tag tag = dao.getTagById(-1L);
        assertNull(tag);
    }

    @Test
    void testRemoveTag() {
        Tag tag = new Tag();
        tag.setTagName("temporary-tag");
        dao.insertTag(tag);

        boolean removed = dao.removeTag(tag);
        assertTrue(removed);

        assertNull(dao.getTagById(tag.getTagId()));

        assertFalse(dao.removeTag(tag));
    }

    @Test
    void testGetAllTags() {
        List<Tag> tags = dao.getAllTags();
        assertNotNull(tags);
        assertTrue(tags.size() >= 3);
        assertTrue(tags.stream().anyMatch(tag -> tag.getTagName().equals("timed")));
    }

}

