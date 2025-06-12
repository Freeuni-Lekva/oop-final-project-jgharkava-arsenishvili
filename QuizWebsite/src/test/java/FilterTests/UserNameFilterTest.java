package FilterTests;

import org.ja.model.Filters.UserNameFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserNameFilterTest {
    private UserNameFilter filter;

    @BeforeEach
    public void setUp() {
        filter = new UserNameFilter("Liza");
    }

    @Test
    public void testBuildWhereClause() {
        String clause = filter.buildWhereClause();
        assertEquals("username like ?", clause);
    }

    @Test
    public void testGetParameters() {
        List<Object> parameters = filter.getParameters();

        assertEquals(1, parameters.size());
        assertEquals("Liza%", parameters.get(0));
    }

    @Test
    public void testGetOrderByClause() {
        String clause = filter.buildOrderByClause();

        assertEquals("true", clause);
    }

}
