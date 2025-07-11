package daoTests;

import org.ja.dao.TagsDao;
import org.ja.utils.Constants;
import org.ja.model.data.Tag;
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
    public void testInsertTagSuccess() {
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
    public void testInsertTagDuplicate() {
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
    public void testGetTagByIdNotFound() {
        Tag tag = dao.getTagById(-1L);
        assertNull(tag);
    }


    @Test
    public void testRemoveTag() {
        Tag tag = new Tag();
        tag.setTagName("temporary-tag");
        dao.insertTag(tag);

        boolean removed = dao.removeTag(tag);
        assertTrue(removed);

        assertNull(dao.getTagById(tag.getTagId()));

        assertFalse(dao.removeTag(tag));
    }


    @Test
    public void testGetAllTags() {
        List<Tag> tags = dao.getAllTags(Constants.FETCH_LIMIT);
        assertNotNull(tags);
        assertTrue(tags.size() >= 3);
        assertTrue(tags.stream().anyMatch(tag -> tag.getTagName().equals("timed")));
    }


    // --- Mockito Tests ---


    @Test
    public void testInsertTag_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Insert failed"));

        TagsDao dao = new TagsDao(ds);
        Tag tag = new Tag(0L, "test");

        assertThrows(RuntimeException.class, () -> dao.insertTag(tag));
    }


    @Test
    public void testRemoveTag_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("Delete failed"));

        TagsDao dao = new TagsDao(ds);
        Tag tag = new Tag(1L, "test");

        assertThrows(RuntimeException.class, () -> dao.removeTag(tag));
    }


    @Test
    public void testGetTagById_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        TagsDao dao = new TagsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTagById(1L));
    }


    @Test
    public void testGetTagByName_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        TagsDao dao = new TagsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getTagByName("test"));
    }


    @Test
    public void testGetAllTags_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("Query failed"));

        TagsDao dao = new TagsDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getAllTags(10));
    }
}

