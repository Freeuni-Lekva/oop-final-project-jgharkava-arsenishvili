package modelTests.dataTests;

import static org.junit.jupiter.api.Assertions.*;

import org.ja.model.data.Tag;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link Tag} class.
 */
public class TagTest {

    @Test
    public void testConstructorWithIdAndName() {
        Tag tag = new Tag(1L, "Science");
        assertEquals(1L, tag.getTagId());
        assertEquals("Science", tag.getTagName());
    }

    @Test
    public void testConstructorWithNameOnly() {
        Tag tag = new Tag("Math");
        assertEquals(0L, tag.getTagId());  // default id is 0
        assertEquals("Math", tag.getTagName());
    }

    @Test
    public void testSettersAndGetters() {
        Tag tag = new Tag();
        tag.setTagId(10L);
        tag.setTagName("History");
        assertEquals(10L, tag.getTagId());
        assertEquals("History", tag.getTagName());
    }

    @Test
    public void testEqualsSameObject() {
        Tag tag = new Tag(1L, "English");
        assertEquals(tag, tag);
    }

    @Test
    public void testEqualsEqualObjects() {
        Tag tag1 = new Tag(2L, "Geography");
        Tag tag2 = new Tag(2L, "Geography");
        assertEquals(tag1, tag2);
        assertEquals(tag2, tag1);
    }

    @Test
    public void testEqualsDifferentObjects() {
        Tag tag1 = new Tag(3L, "Physics");
        Tag tag2 = new Tag(4L, "Physics");
        Tag tag3 = new Tag(3L, "Chemistry");
        assertNotEquals(tag1, tag2);
        assertNotEquals(tag1, tag3);
    }

    @Test
    public void testEqualsNullAndDifferentClass() {
        Tag tag = new Tag(5L, "Biology");
        assertNotEquals(null, tag);
        assertNotEquals("some string", tag);
    }
}

