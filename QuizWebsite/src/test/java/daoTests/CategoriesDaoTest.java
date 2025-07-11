package daoTests;

import org.ja.dao.CategoriesDao;
import org.ja.model.data.Category;
import org.ja.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the CategoriesDao class using an in-memory H2 database.
 */
public class CategoriesDaoTest extends BaseDaoTest{
    private CategoriesDao dao;

    @BeforeEach
    public void setUp() throws Exception {
        setUpDataSource();

        dao = new CategoriesDao(basicDataSource);
    }

    @Test
    public void testGetCategoryById() {

        // Existing
        Category category = dao.getCategoryById(5L);
        assertNotNull(category);
        assertEquals(5, category.getCategoryId());
        assertEquals("Math", category.getCategoryName());

        // Non-existing
        assertNull(dao.getCategoryById(9999L));
    }

    @Test
    public void testGetCategoryByName() {

        // Existing
        Category category = dao.getCategoryByName("Math");
        assertNotNull(category);
        assertEquals(5, category.getCategoryId());

        // Non-existing
        assertNull(dao.getCategoryByName("Dance"));
    }

    @Test
    public void testInsertCategory() {
        Category newCategory = new Category(0, "Science");

        boolean inserted = dao.insertCategory(newCategory);
        assertTrue(inserted, "Category should be inserted successfully");
        assertTrue(newCategory.getCategoryId() > 0, "Inserted category should have a generated ID");

        Category fetched = dao.getCategoryById(newCategory.getCategoryId());
        assertNotNull(fetched);
        assertEquals("Science", fetched.getCategoryName());
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categories = dao.getAllCategories(Constants.FETCH_LIMIT);

        assertNotNull(categories);
        assertTrue(categories.size() >= 4);

        boolean foundMath = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase("Math"));
        assertTrue(foundMath);

        boolean foundMovies = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase("Movies"));
        assertTrue(foundMovies);

        boolean foundTechnology = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase("Technology"));
        assertTrue(foundTechnology);

        boolean foundArt = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase("Art"));
        assertTrue(foundArt);
    }
}
