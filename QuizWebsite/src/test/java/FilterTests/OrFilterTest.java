package FilterTests;

import org.ja.model.Filters.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrFilterTest {
    private OrFilter orFilter;
    private OrderFilter orderFilter;
    private QuizNameFilter quizNameFilter;
    private UserNameFilter userNameFilter;

    @BeforeEach
    public void setUp() {
        orFilter = new OrFilter();
        orderFilter = new OrderFilter("price", OrderFilter.DECREASING);
        quizNameFilter = new QuizNameFilter("MathQuiz");
        userNameFilter = new UserNameFilter("Liza");
    }

    @Test
    public void testEmptyFilter() {
        assertEquals("true", orFilter.buildOrderByClause());
        assertEquals("true", orFilter.buildWhereClause());
        assertTrue(orFilter.getParameters().isEmpty());
    }

    @Test
    public void testBuildWhereClauseOneArgument() {
        orFilter.addFilter(userNameFilter);
        assertEquals("(username like ?)", orFilter.buildWhereClause());
    }

    @Test
    public void testBuildWhereClauseMultipleArguments() {
        orFilter.addFilter(userNameFilter);
        orFilter.addFilter(orderFilter);
        orFilter.addFilter(quizNameFilter);

        assertEquals("(username like ?) or (quiz_name like ?)", orFilter.buildWhereClause());
    }

    @Test
    public void testBuildOrderByClauseOneArgument() {
        orFilter.addFilter(userNameFilter);

        assertEquals("true", orFilter.buildOrderByClause());
    }

    @Test
    public void testBuildOrderByClauseMultipleArguments() {
        orFilter.addFilter(userNameFilter);
        orFilter.addFilter(orderFilter);
        orFilter.addFilter(quizNameFilter);
        orFilter.addFilter(orderFilter);

        assertEquals("price desc, price desc", orFilter.buildOrderByClause());
    }

    @Test
    public void testGetParameters() {
        orFilter.addFilter(quizNameFilter);
        orFilter.addFilter(userNameFilter);
        orFilter.addFilter(userNameFilter);

        assertEquals(Arrays.asList("MathQuiz%", "Liza%", "Liza%"), orFilter.getParameters());
    }

    @Test
    public void testComplexOrFilter() {
        AndFilter andFilter = new AndFilter();
        andFilter.addFilter(orderFilter);
        andFilter.addFilter(userNameFilter);
        andFilter.addFilter(quizNameFilter);
        orFilter.addFilter(andFilter);
        OrderFilter orderFilterAsc = new OrderFilter("age", OrderFilter.INCREASING);
        orFilter.addFilter(orderFilterAsc);
        orFilter.addFilter(userNameFilter);

        assertEquals("((username like ?) and (quiz_name like ?)) or (username like ?)", orFilter.buildWhereClause());
        assertEquals("price desc, age asc", orFilter.buildOrderByClause());
        assertEquals(Arrays.asList("Liza%", "MathQuiz%", "Liza%"), orFilter.getParameters());
    }
}
