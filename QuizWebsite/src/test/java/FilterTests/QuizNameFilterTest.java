package FilterTests;

import org.ja.model.Filters.QuizNameFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QuizNameFilterTest {
    private QuizNameFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new QuizNameFilter("MathQuiz");
    }

    @Test
    public void testBuildWhereClause() {
        String clause = filter.buildWhereClause();
        assertEquals("quiz_name like ?", clause);
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
        assertEquals("MathQuiz%", parameters.get(0));
    }

}
