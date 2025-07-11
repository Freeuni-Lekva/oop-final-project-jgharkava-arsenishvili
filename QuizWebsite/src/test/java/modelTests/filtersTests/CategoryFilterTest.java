package modelTests.filtersTests;

import org.ja.model.filters.CategoryFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link CategoryFilter} class.
 */
public class CategoryFilterTest {
    private CategoryFilter filter;

    @BeforeEach
    public void setUp() {
        this.filter = new CategoryFilter("History");
    }

    @Test
    public void testBuildWhereClause() {
        assertEquals("category_name = ?", filter.buildWhereClause());
    }

    @Test
    public void testBuildOrderByClause() {
        assertEquals("true", filter.buildOrderByClause());
    }

    @Test
    public void testGetParameters() {
        List<Object> parameters = filter.getParameters();

        assertEquals(1, parameters.size());
        assertEquals("History", parameters.get(0));
    }
}
