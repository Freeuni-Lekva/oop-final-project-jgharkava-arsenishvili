package modelTests.filtersTests;

import org.ja.model.filters.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the {@link AndFilter} class.
 */
public class AndFilterTest {
    private AndFilter andFilter;
    private OrderFilter orderFilter;
    private QuizNameFilter quizNameFilter;
    private UserNameFilter userNameFilter;

    @BeforeEach
    public void setUp() {
        andFilter = new AndFilter();
        orderFilter = new OrderFilter("price", OrderFilter.DECREASING);
        quizNameFilter = new QuizNameFilter("MathQuiz");
        userNameFilter = new UserNameFilter("Liza");
    }

    @Test
    public void testEmptyFilter() {
        assertEquals("true", andFilter.buildOrderByClause());
        assertEquals("true", andFilter.buildWhereClause());
        assertTrue(andFilter.getParameters().isEmpty());
    }

    @Test
    public void testBuildWhereClauseOneArgument() {
        andFilter.addFilter(userNameFilter);
        assertEquals("(username like ?)", andFilter.buildWhereClause());
    }

    @Test
    public void testBuildWhereClauseMultipleArguments() {
        andFilter.addFilter(userNameFilter);
        andFilter.addFilter(orderFilter);
        andFilter.addFilter(quizNameFilter);

        assertEquals("(username like ?) and (quiz_name like ?)", andFilter.buildWhereClause());
    }

    @Test
    public void testBuildOrderByClauseOneArgument() {
        andFilter.addFilter(userNameFilter);

        assertEquals("true", andFilter.buildOrderByClause());
    }

    @Test
    public void testBuildOrderByClauseMultipleArguments() {
        andFilter.addFilter(userNameFilter);
        andFilter.addFilter(orderFilter);
        andFilter.addFilter(quizNameFilter);
        andFilter.addFilter(orderFilter);

        assertEquals("price desc, price desc", andFilter.buildOrderByClause());
    }

    @Test
    public void testGetParameters() {
        andFilter.addFilter(quizNameFilter);
        andFilter.addFilter(userNameFilter);
        andFilter.addFilter(userNameFilter);

        assertEquals(Arrays.asList("MathQuiz%", "Liza%", "Liza%"), andFilter.getParameters());
    }

    @Test
    public void testComplexAndFilter() {
        OrFilter orFilter = new OrFilter();
        orFilter.addFilter(userNameFilter);
        orFilter.addFilter(quizNameFilter);
        orFilter.addFilter(orderFilter);
        andFilter.addFilter(quizNameFilter);
        andFilter.addFilter(orderFilter);
        andFilter.addFilter(orFilter);


        assertEquals("(quiz_name like ?) and ((username like ?) or (quiz_name like ?))", andFilter.buildWhereClause());
        assertEquals(Arrays.asList("MathQuiz%", "Liza%", "MathQuiz%" ), andFilter.getParameters());
        assertEquals("price desc, price desc", andFilter.buildOrderByClause());
    }
}
