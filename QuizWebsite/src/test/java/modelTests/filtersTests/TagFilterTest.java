package modelTests.filtersTests;

import org.ja.model.filters.TagFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Unit tests for the {@link TagFilter} class.
 */
public class TagFilterTest {
    private TagFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new TagFilter("fun");
    }

    @Test
    public void testBuildWhereClause() {
        assertEquals("tag_name = ?", filter.buildWhereClause());
    }

    @Test
    public void testBuildOrderByClause() {
        assertEquals("true", filter.buildOrderByClause());
    }

    @Test
    public void testGetParameters() {
        List<Object> parameters = filter.getParameters();

        assertEquals(1, parameters.size());
        assertEquals("fun", parameters.get(0));
    }
}
