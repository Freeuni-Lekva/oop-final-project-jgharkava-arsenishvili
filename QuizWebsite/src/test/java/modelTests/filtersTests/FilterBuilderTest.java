package modelTests.filtersTests;

import org.ja.model.filters.Filter;
import org.ja.model.filters.FilterBuilder;
import org.ja.utils.Constants;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for the {@link FilterBuilder} class.
 */
public class FilterBuilderTest {

    @Test
    public void testQuizNameOnly() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(Constants.FilterFields.QUIZ_NAME, "Liza");

        Filter filter = FilterBuilder.build(request);

        assertEquals("(quiz_name like ?)", filter.buildWhereClause());
        assertEquals("true", filter.buildOrderByClause());
        assertEquals(List.of("Liza%"), filter.getParameters());
    }

    @Test
    public void testTagAndOrder() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(Constants.FilterFields.TAG, "Fun", "Interesting");
        request.addParameter(Constants.FilterFields.ORDER, "rating");

        Filter filter = FilterBuilder.build(request);

        assertEquals("((tag_name = ?) or (tag_name = ?))", filter.buildWhereClause());
        assertEquals("rating desc", filter.buildOrderByClause());
        assertEquals(List.of("Fun", "Interesting"), filter.getParameters());
    }

    @Test
    public void testComplexFilters() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(Constants.FilterFields.QUIZ_NAME, "Liza");
        request.addParameter(Constants.FilterFields.QUIZ_NAME, "MathQuiz");
        request.addParameter(Constants.FilterFields.CATEGORY, "Geometry", "Math");
        request.addParameter(Constants.FilterFields.TAG, "Fun", "Interesting");
        request.addParameter(Constants.FilterFields.ORDER, "rating");

        Filter filter = FilterBuilder.build(request);

        assertEquals("(quiz_name like ?) and (quiz_name like ?) and ((category_name = ?) or (category_name = ?) or (tag_name = ?) or (tag_name = ?))", filter.buildWhereClause());
        assertEquals("rating desc", filter.buildOrderByClause());
        assertEquals(Arrays.asList("Liza%", "MathQuiz%", "Geometry", "Math", "Fun", "Interesting"), filter.getParameters());
    }

}
