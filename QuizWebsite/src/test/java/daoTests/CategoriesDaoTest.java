package daoTests;

import org.ja.dao.CategoriesDao;
import org.ja.model.data.Category;
import org.ja.utils.Constants;
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


    // --- Mockito Tests ---


    @Test
    public void testInsertCategory_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString(), eq(PreparedStatement.RETURN_GENERATED_KEYS))).thenReturn(ps);
        when(ps.executeUpdate()).thenThrow(new SQLException("insert failed"));

        CategoriesDao dao = new CategoriesDao(ds);
        Category category = new Category(0L, "");
        category.setCategoryName("Test");

        assertThrows(RuntimeException.class, () -> dao.insertCategory(category));
    }


    @Test
    public void testGetCategoryById_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("query failed"));

        CategoriesDao dao = new CategoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getCategoryById(1));
    }


    @Test
    public void testGetCategoryByName_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("query by name failed"));

        CategoriesDao dao = new CategoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getCategoryByName("Test"));
    }


    @Test
    public void testGetAllCategories_throwsException() throws Exception {
        BasicDataSource ds = mock(BasicDataSource.class);
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);

        when(ds.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenThrow(new SQLException("get all failed"));

        CategoriesDao dao = new CategoriesDao(ds);

        assertThrows(RuntimeException.class, () -> dao.getAllCategories(5));
    }
}
