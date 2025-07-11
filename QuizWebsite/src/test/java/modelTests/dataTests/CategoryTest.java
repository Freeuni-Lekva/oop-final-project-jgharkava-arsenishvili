package modelTests.dataTests;

import org.ja.model.data.Category;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Category} class.
 */
public class CategoryTest {

    @Test
    public void testConstructorWithNameOnly() {
        Category category = new Category("Science");
        assertEquals("Science", category.getCategoryName());
        assertEquals(0, category.getCategoryId()); // default long = 0
    }

    @Test
    public void testConstructorWithIdAndName() {
        Category category = new Category(10L, "Math");
        assertEquals(10L, category.getCategoryId());
        assertEquals("Math", category.getCategoryName());
    }

    @Test
    public void testSetCategoryId() {
        Category category = new Category("History");
        category.setCategoryId(5L);
        assertEquals(5L, category.getCategoryId());
    }

    @Test
    public void testEqualsSameObject() {
        Category category = new Category(1L, "Biology");
        assertEquals(category, category);
    }

    @Test
    public void testEqualsEqualObjects() {
        Category c1 = new Category(2L, "Literature");
        Category c2 = new Category(2L, "Literature");
        assertEquals(c1, c2);
    }

    @Test
    public void testEqualsDifferentId() {
        Category c1 = new Category(3L, "Physics");
        Category c2 = new Category(4L, "Physics");
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEqualsDifferentName() {
        Category c1 = new Category(3L, "Physics");
        Category c2 = new Category(3L, "Chemistry");
        assertNotEquals(c1, c2);
    }

    @Test
    public void testEqualsNullAndOtherType() {
        Category category = new Category(1L, "Geography");
        assertNotEquals(null, category);
        assertNotEquals("Geography", category);
    }
}
