package FilterTests;

import org.ja.model.filters.QuizNameFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link QuizNameFilter} class.
 */
public class QuizNameFilterTest {
    private QuizNameFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new QuizNameFilter("MathQuiz");
    }

    @Test
    public void testBuildWhereClause() {
        assertEquals("quiz_name like ?", filter.buildWhereClause());
    }

    @Test
    public void testBuildOrderByClause() {
        assertEquals("true", filter.buildOrderByClause());
    }

    @Test
    public void testGetParameters() {
        List<Object> parameters = filter.getParameters();

        assertEquals(1, parameters.size());
        assertEquals("MathQuiz%", parameters.get(0));
    }

}
