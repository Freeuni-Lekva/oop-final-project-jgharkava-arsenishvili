package FilterTests;

import org.ja.model.Filters.TagFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagFilterTest {
    private TagFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new TagFilter("fun");
    }

    @Test
    public void testBuildWhereClause() {
        String clause = filter.buildWhereClause();
        assertEquals("tag_name = ?", clause);
    }

    @Test
    public void testBuildOrderByClause() {
        String clause = filter.buildOrderByClause();
        assertEquals("true", clause);
    }

    @Test
    public void testGetParameters() {
        List<Object> parameters = filter.getParameters();

        assertEquals(1, parameters.size());
        assertEquals("fun", parameters.get(0));
    }
}
